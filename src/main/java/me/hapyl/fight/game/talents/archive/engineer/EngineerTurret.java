package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;

public class EngineerTurret extends EngineerTalent {

    // This field delegates to the Sentry class,
    // it's quite clear what damage it is, unless
    // there are more causes of damage
    @DisplayField
    private final double damage = 5; // prev: sDamage
    @DisplayField private final double radius = 16;
    @DisplayField private int delayBetweenShots = 20;

    public EngineerTurret() {
        super("Sentry", 6);

        setDescription("""
                Create a &cSentry&7 that will shoot the &enearest &cenemy&7.
                """);

        setItem(Material.NETHERITE_SCRAP);
        setCooldownSec(35);
    }

    @Nonnull
    @Override
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new Construct(player, location, this) {
            @Override
            public void onCreate() {
            }

            @Nonnull
            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(15d, 25d, 35d, 45d);
            }

            @Nonnull
            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(15, 25, 35, 45);
            }

            @Override
            public void onDestroy() {
            }

            @Override
            public void onTick() {
                final LivingGameEntity nearestEntity = Collect.nearestEntity(location, radius, entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return false;
                    }

                    return entity.hasLineOfSight(this.entity.getEntity());
                });

                // Rotate if there are no entities
                if (nearestEntity == null) {
                    final ArmorStand stand = entity.getStand();
                    final Location location = stand.getLocation();
                    location.setYaw(location.getYaw() + 5);

                    stand.teleport(location);
                    return;
                }

                if (!modulo(delayBetweenShots)) {
                    return;
                }

                entity.lookAt(nearestEntity.getLocation());

                new RaycastTask(entity.getLocation().add(0.00d, 1.5d, 0.00d)) {

                    @Override
                    public boolean predicate(@Nonnull Location location) {
                        final Block block = location.getBlock();
                        final Material type = block.getType();


                        return !type.isOccluding();
                    }

                    @Override
                    public boolean step(@Nonnull Location location) {
                        player.spawnWorldParticle(location, Particle.CRIT_MAGIC, 1);

                        // Hit detection
                        final LivingGameEntity targetEntity = Collect.nearestEntity(location, 1, entity -> {
                            return !player.isSelfOrTeammate(entity);
                        });

                        if (targetEntity == null) {
                            return false;
                        }

                        targetEntity.damage(damage, player, EnumDamageCause.SENTRY_SHOT);
                        return true;
                    }
                }.runTaskTimer(0, 1);

                // Fx
                player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 1.25f);
                player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 2.0f);
            }
        };

    }


}
