package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
import me.hapyl.fight.game.entity.event.EventType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class Voidgloom extends GameEntityType<Enderman> implements Listener {

    public Voidgloom() {
        super("Voidgloom Seraph", Enderman.class);

        final Attributes attributes = getAttributes();
        attributes.setHealth(1000);
        attributes.setDefense(50);

        setType(EntityType.MINIBOSS);
    }

    @EventHandler()
    public void handleEntityTeleport(EntityTeleportEvent ev) {
        final Entity entity = ev.getEntity();

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        CF.getEntity(living, VoidgloomEntity.class).ifPresent(gameEntity -> {

        });
    }

    @Override
    public double getHologramOffset() {
        return 0.33d;
    }

    @Nonnull
    @Override
    public NamedGameEntity<Enderman> create(@Nonnull Enderman entity) {
        return new VoidgloomEntity(this, entity);
    }

    private static class VoidgloomEntity extends NamedGameEntity<Enderman> {

        private static final int MAX_HITS = 15;
        private static final int NO_DAMAGE_TICKS = 10;
        private static final int NO_DAMAGE_TICKS_HIT = 2;

        private final double HITS_THRESHOLD = getMaxHealth() / 3;

        private boolean allowTeleport = false;
        private boolean hitPhase = true;
        private int hitsLeft = MAX_HITS;
        private double damageTook = 0;

        public VoidgloomEntity(GameEntityType<Enderman> type, Enderman entity) {
            super(type, entity);

            listenTo(EventType.ENTITY_TELEPORT, ev -> {
                if (allowTeleport) {
                    allowTeleport = false;
                    return;
                }

                ev.setCancelled(true);
            });
        }

        @Override
        public void onTick() {
            if (tick % 10 == 0) {
                spawnWorldParticle(getLocation().add(0.0d, getEyeHeight(), 0.0d), Particle.SPELL_WITCH, 10, 0.1, 0.2, 0.1, 0.05f);
            }
        }

        @Override
        public void onSpawn() {
            entity.setMaximumNoDamageTicks(NO_DAMAGE_TICKS);

            setKnockback(0.85d);
            setTargetClosest();
        }

        @Override
        public DamageOutput onDamageTaken(@Nonnull DamageInput input) {
            if (hitPhase) {
                hitsLeft--;

                final Location location = getLocation();

                if (hitsLeft <= 0) {
                    hitPhase = false;
                    damageTook = 0;

                    entity.setMaximumNoDamageTicks(NO_DAMAGE_TICKS);

                    // Fx
                    playPlayerSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.75f);
                    spawnWorldParticle(location, Particle.EXPLOSION_LARGE, 3);
                    return null;
                }

                playPlayerSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f - (1.0f / MAX_HITS * hitsLeft));
                return DamageOutput.CANCEL;
            }
            else {
                damageTook += input.getDamage();

                if (damageTook >= HITS_THRESHOLD) {
                    hitsLeft = MAX_HITS;
                    entity.setMaximumNoDamageTicks(NO_DAMAGE_TICKS_HIT);
                    hitPhase = true;
                }
            }

            return null;
        }

        @Override
        public String getHealthFormatted() {
            if (hitPhase) {
                return Color.DARK_PURPLE.lighten((float) MAX_HITS / hitsLeft) + "&l%s Hits".formatted(hitsLeft);
            }
            else {
                return super.getHealthFormatted();
            }
        }
    }
}
