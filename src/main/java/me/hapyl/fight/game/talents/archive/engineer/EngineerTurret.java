package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class EngineerTurret extends EngineerTalent {
    @DisplayField
    private final double sDamage = 7.2;
    public EngineerTurret() {
        super("Sentry", 6);

        setDescription("""
                Create a Sentry.
                It will shoot any player it sees!
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

          //      stand.setHelmet(new ItemStack(Material.END_ROD));
                // hapyl, no


            }

            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(15d, 25d, 35d, 45d);
            }

            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(15, 25, 35, 45);
            }

            @Override
            public void onDestroy() {
                player.sendMessage("&cYour previous &lSentry &cwas destroyed!");
            }

            @Override
            public void onTick() {
                if(modulo(20)) {

                    LivingGameEntity nearestEntity = Collect.nearestEntity(location, 16, livingGameEntity -> {
                        if (livingGameEntity.equals(player)) {
                            return false;
                        }
                        if (!livingGameEntity.hasLineOfSight(stand)) {
                            return false;
                        }
                        if (player.isTeammate(livingGameEntity)) {
                            return false;
                        }
                        return true;

                    });

                    if (nearestEntity == null) {
                        return;
                    }
                    CFUtils.lookAt(stand, nearestEntity.getLocation());
                    new RaycastTask(stand.getLocation().add(0.00d, 1.5d, 0.00d)) {

                        @Override
                        public boolean predicate(@Nonnull Location location) {
                            final Block block = location.getBlock();
                            final Material type = block.getType();


                            return !type.isOccluding();
                        }

                        @Override
                        public boolean step(@Nonnull Location location) {
                            PlayerLib.spawnParticle(location, Particle.CRIT_MAGIC, 1);

                            LivingGameEntity targetEntity = Collect.nearestEntity(location, 2, livingGameEntity -> {
                                if (livingGameEntity.equals(player)) {
                                    return false;
                                }
                                if (player.isTeammate(livingGameEntity)) {
                                    return false;
                                }
                                return true;
                            });

                            if (targetEntity == null) {
                                return false;
                            }

                            targetEntity.damage(sDamage, player, EnumDamageCause.SENTRY_SHOT);
                            return true;
                        }
                    }.runTaskTimer(0, 1);

                }
                else{
                    Location standLocation = stand.getLocation();
                    standLocation.setYaw(standLocation.getYaw()+1);
                    stand.teleport(standLocation);
                }
            }
        };

    }



    @Nonnull
    @Override
    public Response predicate(@Nonnull GamePlayer player, @Nonnull Location location) {
        return Response.OK;
    }
}
