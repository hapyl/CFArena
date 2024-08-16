package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.LivingGenericGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Zombie;

import javax.annotation.Nonnull;

public class LaserZombie extends TamerPack {

    @DisplayField private final int laserPeriod = 60;
    @DisplayField(percentage = true) private final double laserBaseDefenseReduction = 0.2d;
    @DisplayField private final int laserDefenseReductionDuration = 100;
    @DisplayField private final int laserHitDelay = 10;

    public LaserZombie() {
        super("Laser Zombie", """
                Lasers target enemy, reducing their %s.
                """.formatted(AttributeType.DEFENSE), TalentType.IMPAIR);

        attributes.setHealth(20);
        attributes.setSpeed(50);
        attributes.setKnockbackResistance(1.0d);

        setDurationSec(20);
    }

    @Override
    public void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location) {
        pack.createEntity(location, Entities.ZOMBIE, entity -> new LaserZombieEntity(pack, entity));
    }

    private class LaserZombieEntity extends TamerEntity<Zombie> {

        private final LivingGenericGameEntity<Guardian> guardian;

        public LaserZombieEntity(@Nonnull ActiveTamerPack pack, @Nonnull Zombie entity) {
            super(pack, entity);

            entity.setAdult();
            final Location location = entity.getLocation();

            guardian = pack.player.spawnAlliedEntity(location, Entities.GUARDIAN, e -> new LivingGenericGameEntity<>(e) {
                @Override
                public void kill() {
                    entity.remove();
                    super.kill();
                }
            });

            guardian.setInvulnerable(true);
            guardian.setAI(false);
        }

        @Override
        public void tick(int index) {
            super.tick(index);

            final Location eyeLocation = entity.getEyeLocation();
            eyeLocation.subtract(0.0d, 0.25d, 0.d);

            guardian.teleport(eyeLocation);

            final LivingGameEntity target = getTargetEntity();

            if (target != null && guardian.hasLineOfSight(target) && tick > 0 && tick % laserPeriod == 0) {
                guardian.setTarget(target);
                guardian.entity.setLaser(true);
                guardian.entity.setLaserTicks(80 - laserHitDelay);

                player.schedule(() -> {
                    guardian.setTarget(null);
                    guardian.entity.setLaser(false);

                    final EntityAttributes attributes = target.getAttributes();
                    attributes.decreaseTemporary(
                            Temper.TAMER_LASER,
                            AttributeType.DEFENSE,
                            scaleUltimateEffectiveness(player, laserBaseDefenseReduction),
                            laserDefenseReductionDuration, player
                    );

                    // Fx
                    guardian.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);
                }, laserHitDelay);
            }
        }

        @Override
        public void kill() {
            super.kill();
            guardian.remove();
        }

        @Override
        public void remove() {
            super.remove();
            guardian.remove();
        }
    }
}
