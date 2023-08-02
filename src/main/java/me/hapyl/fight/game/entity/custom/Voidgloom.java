package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntities;
import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
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

    private static final int MAX_HITS = 15;

    public Voidgloom() {
        super("Voidgloom Seraph", Enderman.class);

        final Attributes attributes = getAttributes();
        attributes.setHealth(200);
        attributes.setDefense(50);

        setType(EntityType.BOSS);
    }

    @EventHandler()
    public void handleEntityTeleport(EntityTeleportEvent ev) {
        final Entity entity = ev.getEntity();

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        CF.getEntityOptional(living).ifPresent(gameEntity -> {
            if (gameEntity.isType(GameEntities.VOIDGLOOM)) {
                ev.setCancelled(true);
            }
        });
    }

    @Override
    public double getHologramOffset() {
        return 0.33d;
    }

    @Nonnull
    @Override
    public NamedGameEntity<Enderman> create(@Nonnull Enderman entity) {
        return new Instance(this, entity);
    }

    private static class Instance extends NamedGameEntity<Enderman> {

        private static final int NO_DAMAGE_TICKS = 10;
        private static final int NO_DAMAGE_TICKS_HIT = 2;

        private boolean hitPhase = true;
        private int hitsLeft = MAX_HITS;
        private double damageTook = 0;

        public Instance(GameEntityType<Enderman> type, Enderman entity) {
            super(type, entity);
        }

        @Override
        public void onTick() {
            if (tick % 10 == 0) {
                spawnParticle(getLocation().add(0.0d, getEyeHeight(), 0.0d), Particle.SPELL_WITCH, 10, 0.1, 0.2, 0.1, 0.05f);
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
                    playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.75f);
                    spawnParticle(location, Particle.EXPLOSION_LARGE, 3);
                    return null;
                }

                playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f - (1.0f / MAX_HITS * hitsLeft));
                return DamageOutput.CANCEL;
            }
            else {
                damageTook += input.getDamage();

                if (damageTook >= 100) {
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
