package me.hapyl.fight.game.talents.taker;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.LinkedList;

public class TakerHook {

    private final int EXTEND_SPEED = 3;
    private final int CONTRACT_SPEED = 2;

    private final GamePlayer player;
    private final LinkedList<ArmorStand> chains;
    private final double HEIGHT = 1.5d;

    private LivingGameEntity hooked;
    private GameTask taskExtend;
    private GameTask taskContract;

    public TakerHook(GamePlayer player) {
        this.player = player;
        this.chains = Lists.newLinkedList();
        this.hooked = null;

        extend();
    }

    public void extend() {
        final Location location = player.getEyeLocation().subtract(0.0d, 0.5d, 0.0d);
        final Vector vector = location.getDirection().normalize();

        player.addEffect(Effects.SLOW, 10, 10000);
        player.setCanMove(false);

        taskExtend = new GameTask() {
            private double step = 0.0d;

            @Override
            public void run() {
                if (step >= talent().getMaxDistanceScaled()) {
                    contract();
                    return;
                }

                // Create multiple chains to make extending faster
                for (int i = 0; i < EXTEND_SPEED; i++) {
                    nextChain();
                }
            }

            private void nextChain() {
                if (hooked != null) {
                    return; // don't create if hooked something
                }

                final double x = vector.getX() * step;
                final double y = vector.getY() * step;
                final double z = vector.getZ() * step;

                location.add(x, y, z);

                if (location.getBlock().getType().isOccluding()) {
                    contract();
                    return;
                }

                final LivingGameEntity nearest = Collect.nearestEntity(location, 1.5d, player);

                if (nearest != null) {
                    if (player.isSelfOrTeammateOrHasEffectResistance(nearest)) {
                        contract();
                        return;
                    }

                    hooked = nearest;
                    double health = hooked.getHealth();

                    nearest.sendMessage(
                            "&4☠ &cOuch! %s hooked you, and you lost &e%s%%&c of your health!",
                            player.getName(),
                            talent().damagePercent
                    );

                    nearest.addEffect(Effects.SLOW, 1, 60);
                    nearest.addEffect(Effects.WITHER, 1, 60);

                    final double damage = Math.min(health * (talent().damagePercent / 100), 100.0d);
                    nearest.damage(damage, player);

                    // Reduce cooldown
                    talent().reduceCooldown(player);

                    contract();
                    return;
                }

                createChain(location);
                step += talent().shift;

                location.subtract(x, y, z);
            }
        }.runTaskTimer(0, 1);
    }

    public void remove() {
        Nulls.runIfNotNull(taskExtend, GameTask::cancel);
        Nulls.runIfNotNull(taskContract, GameTask::cancel);

        CFUtils.clearCollection(chains);

        player.removeEffect(Effects.SLOW);
        player.removeEffect(Effects.JUMP_BOOST);

        player.setCanMove(true);
    }

    public void breakChains() {
        if (chains.isEmpty()) {
            return;
        }

        chains.forEach(stand -> {
            final EulerAngle pose = stand.getHeadPose();

            stand.setHeadPose(pose.add(player.random.nextDouble(), player.random.nextDouble(), player.random.nextDouble()));

            PlayerLib.playSound(stand.getLocation(), Sound.BLOCK_CHAIN_BREAK, 0.0f);
        });

        player.sendSubtitle("&8\uD83D\uDD17 Broke Chains!", 0, 15, 0);

        new GameTask() {
            @Override
            public void run() {
                remove();
            }
        }.runTaskLater(5).runTaskAtCancel();
    }

    private void contract() {
        taskExtend.cancel();

        taskContract = new GameTask() {

            @Override
            public void run() {
                if (chains.isEmpty()) {
                    player.removeEffect(Effects.SLOW);
                    player.setCanMove(true);

                    cancel();
                    return;
                }

                ArmorStand last = chains.peekLast();

                for (int i = 0; i < CONTRACT_SPEED; i++) {
                    final ArmorStand poll = chains.pollLast();

                    if (poll == null) {
                        break;
                    }
                    else {
                        if (last != null) {
                            last.remove();
                        }

                        last = poll;
                    }
                }

                final Location location = last.getLocation().add(0.0d, HEIGHT, 0.0d);

                if (hooked != null) {
                    final Location teleportLocation = location.clone();
                    BukkitUtils.mergePitchYaw(hooked.getLocation(), teleportLocation);
                    hooked.teleport(teleportLocation);
                }

                last.remove();

                // Fx
                player.playWorldSound(location, Sound.BLOCK_CHAIN_BREAK, 1.0f);
            }
        }.runTaskTimer(0, 1);

    }

    private DeathSwap talent() {
        return HeroRegistry.TAKER.getSecondTalent();
    }

    private void createChain(Location location) {
        chains.offerLast(Entities.ARMOR_STAND_MARKER.spawn(location.clone().subtract(0.0d, HEIGHT, 0.0d), self -> {
            self.setVisible(false);
            self.setSilent(true);

            CFUtils.setEquipment(self, equipment -> {
                equipment.setHelmet(new ItemStack(Material.CHAIN));
                self.setHeadPose(new EulerAngle(Math.toRadians(location.getPitch() + 90.0d), 0.0d, 0.0d));
            });
        }));

        player.playWorldSound(location, Sound.BLOCK_CHAIN_PLACE, 1.0f);
    }
}
