package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class Voidgloom extends NamedEntityType implements Listener {

    public Voidgloom(@Nonnull Key key) {
        super(key, "Voidgloom Seraph");

        final BaseAttributes attributes = getAttributes();
        attributes.setMaxHealth(1000);
        attributes.setDefense(50);

        setType(EntityType.MINIBOSS);
    }

    @EventHandler()
    public void handleEntityTeleport(EntityTeleportEvent ev) {
        final org.bukkit.entity.Entity entity = ev.getEntity();
        final LivingGameEntity gameEntity = CF.getEntity(entity);

        if (gameEntity instanceof Entity voidgloomEntity) {
            if (voidgloomEntity.allowTeleport) {
                voidgloomEntity.allowTeleport = false;
                return;
            }

            ev.setCancelled(true);
        }
    }

    @Nonnull
    @Override
    public NamedGameEntity<?> create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.ENDERMAN, Entity::new);
    }

    @Override
    public double getHologramOffset() {
        return 0.33d;
    }

    private class Entity extends NamedGameEntity<Enderman> {

        private static final int MAX_HITS = 15;
        private static final int NO_DAMAGE_TICKS = 10;
        private static final int NO_DAMAGE_TICKS_HIT = 2;

        private final double HITS_THRESHOLD = getMaxHealth() / 3;

        private boolean allowTeleport = false;
        private boolean hitPhase = true;
        private int hitsLeft = MAX_HITS;
        private double damageTook = 0;

        public Entity(Enderman entity) {
            super(Voidgloom.this, entity);
        }

        @Override
        public void onTick(int tick) {
            if (tick % 10 == 0) {
                spawnWorldParticle(getLocation().add(0.0d, getEyeHeight(), 0.0d), Particle.WITCH, 10, 0.1, 0.2, 0.1, 0.05f);
            }
        }

        @Override
        public void onSpawn() {
            overrideNoDamageTicks = NO_DAMAGE_TICKS;

            setKnockback(0.85d);
            setTargetClosest();
        }

        @Override
        public void onDamageTaken(@Nonnull DamageInstance instance) {
            if (hitPhase) {
                hitsLeft--;

                final Location location = getLocation();

                if (hitsLeft <= 0) {
                    hitPhase = false;
                    damageTook = 0;

                    overrideNoDamageTicks = NO_DAMAGE_TICKS;

                    // Fx
                    playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.75f);
                    spawnWorldParticle(Particle.EXPLOSION_EMITTER, 3);
                    return;
                }

                playWorldSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f - (1.0f / MAX_HITS * hitsLeft));
                instance.setCancelled(true);
            }
            else {
                damageTook += instance.getDamage();

                if (damageTook >= HITS_THRESHOLD) {
                    hitsLeft = MAX_HITS;
                    overrideNoDamageTicks = NO_DAMAGE_TICKS_HIT;
                    hitPhase = true;
                }
            }
        }

        @Override
        public @Nonnull String getHealthFormatted() {
            if (hitPhase) {
                return Color.DEEP_PURPLE.lighten((float) MAX_HITS / hitsLeft) + "&l%s Hits".formatted(hitsLeft);
            }
            else {
                return super.getHealthFormatted();
            }
        }
    }
}
