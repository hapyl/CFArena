package me.hapyl.fight.game.entity.commission.hypixel;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class Voidgloom extends CommissionEntityType implements Listener {

    public Voidgloom(@Nonnull Key key) {
        super(
                key, "Voidgloom Seraph", new BaseAttributes()
                        .put(AttributeType.MAX_HEALTH, 1000)
                        .put(AttributeType.DEFENSE, 5)
        );

        setType(EntityType.MINIBOSS);
    }

    @EventHandler()
    public void handleEntityTeleport(EntityTeleportEvent ev) {
        final org.bukkit.entity.Entity entity = ev.getEntity();
        final LivingGameEntity gameEntity = CF.getEntity(entity);

        if (gameEntity instanceof VoidgloomEntity voidgloomEntity) {
            if (voidgloomEntity.allowTeleport) {
                voidgloomEntity.allowTeleport = false;
                return;
            }

            ev.setCancelled(true);
        }
    }

    @Nonnull
    @Override
    public CommissionEntity create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.ENDERMAN, VoidgloomEntity::new);
    }

    @Override
    public double getHologramOffset() {
        return 0.33d;
    }

    public class VoidgloomEntity extends CommissionEntity {

        private static final int MAX_HITS = 15;
        private static final int NO_DAMAGE_TICKS = 10;
        private static final int NO_DAMAGE_TICKS_HIT = 2;

        private boolean allowTeleport = false;
        private boolean hitPhase = true;

        private int hitsLeft = MAX_HITS;
        private double damageTook = 0;

        public VoidgloomEntity(Enderman entity) {
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
            setKnockback(0.85d);
            setTargetClosest();
        }

        @Override
        public void onDamageTaken(@Nonnull DamageInstance instance) {
            final double hitsThreshold = getMaxHealth() / 3d;

            if (hitPhase) {
                hitsLeft--;

                final Location location = getLocation();

                if (hitsLeft <= 0) {
                    hitPhase = false;
                    damageTook = 0;

                    // Fx
                    playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.75f);
                    spawnWorldParticle(Particle.EXPLOSION_EMITTER, 3);
                }
                else {
                    playWorldSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f - (1.0f / MAX_HITS * hitsLeft));
                }

                instance.setCancelled(true);
            }
            else {
                damageTook += instance.getDamage();

                if (damageTook >= hitsThreshold) {
                    hitsLeft = MAX_HITS;
                    hitPhase = true;
                }
            }
        }

        @Override
        @Nonnull
        public String getHealthFormatted() {
            if (hitPhase) {
                return Color.DEEP_PURPLE.lighten((float) MAX_HITS / hitsLeft) + "&l%s Hits".formatted(hitsLeft);
            }
            else {
                return super.getHealthFormatted();
            }
        }

        @Override
        protected boolean shouldStartAttackCooldown() {
            return !hitPhase;
        }
    }
}
