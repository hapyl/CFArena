package me.hapyl.fight.game.talents.archive.taker;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.taker.Taker;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.LinkedList;

public class TakerHook {

    private final int EXTEND_SPEED = 3;
    private final int CONTRACT_SPEED = 2;

    private final Player player;
    private final LinkedList<ArmorStand> chains;
    private final double HEIGHT = 1.5d;

    private LivingEntity hooked;
    private GameTask taskExtend;
    private GameTask taskContract;

    public TakerHook(Player player) {
        this.player = player;
        this.chains = Lists.newLinkedList();
        this.hooked = null;

        extend();
    }

    public void extend() {
        final Location location = player.getEyeLocation().subtract(0.0d, 0.5d, 0.0d);
        final Vector vector = location.getDirection().normalize();

        PlayerLib.addEffect(player, PotionEffectType.SLOW, 10000, 10);
        //PlayerLib.addEffect(player, PotionEffectType.JUMP, 10000, 250);

        GamePlayer.getPlayer(player).setCanMove(false);

        taskExtend = new GameTask() {
            private double step = 0.0d;

            private void nextChain() {
                if (hooked != null) {
                    return; // don't create if hooked something
                }

                final double x = vector.getX() * step;
                final double y = vector.getY() * step;
                final double z = vector.getZ() * step;

                location.add(x, y, z);

                if (!location.getBlock().getType().isAir()) {
                    contract();
                    return;
                }

                final LivingEntity nearest = Collect.nearestLivingEntity(location, 1.5d, player);

                if (nearest != null) {
                    hooked = nearest;
                    double health = hooked.getHealth();

                    if (hooked instanceof Player hookedPlayer) {
                        health = GamePlayer.getPlayer(hookedPlayer).getHealth();

                        Chat.sendMessage(
                                hookedPlayer,
                                "&4☠ &cOuch! %s hooked you and you lost &e%s%%&c of you health!",
                                player.getName(),
                                talent().damagePercent
                        );

                        PlayerLib.addEffect(hookedPlayer, PotionEffectType.SLOW, 60, 1);
                        PlayerLib.addEffect(hookedPlayer, PotionEffectType.WITHER, 60, 1);
                    }

                    final double damage = Math.min(health * (talent().damagePercent / 100), 100.0d);
                    GamePlayer.damageEntity(hooked, damage, player);

                    // Reduce cooldown
                    talent().reduceCooldown(player);

                    contract();
                    return;
                }

                createChain(location);
                step += talent().shift;

                location.subtract(x, y, z);
            }

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
        }.runTaskTimer(0, 1);
    }

    private void contract() {
        taskExtend.cancel();

        taskContract = new GameTask() {

            @Override
            public void run() {
                if (chains.isEmpty()) {
                    player.removePotionEffect(PotionEffectType.SLOW);
                    //player.removePotionEffect(PotionEffectType.JUMP);

                    GamePlayer.getPlayer(player).setCanMove(true);

                    chains.clear();
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

                //PlayerLib.spawnParticle(location, Particle.CRIT, 1);
                PlayerLib.playSound(location, Sound.BLOCK_CHAIN_BREAK, 1.0f);

                last.remove();
            }
        }.runTaskTimer(0, 1);

    }

    private DeathSwap talent() {
        return Heroes.TAKER.getHero(Taker.class).getSecondTalent();
    }

    private void createChain(Location location) {
        chains.offerLast(Entities.ARMOR_STAND_MARKER.spawn(location.clone().subtract(0.0d, HEIGHT, 0.0d), self -> {
            self.setVisible(false);
            self.setSilent(true);

            Utils.setEquipment(self, equipment -> {
                equipment.setHelmet(new ItemStack(Material.CHAIN));
                self.setHeadPose(new EulerAngle(Math.toRadians(location.getPitch() + 90.0d), 0.0d, 0.0d));
            });
        }));

        PlayerLib.playSound(location, Sound.BLOCK_CHAIN_PLACE, 1.0f);
    }

    public void remove() {
        Nulls.runIfNotNull(taskExtend, GameTask::cancel);
        Nulls.runIfNotNull(taskContract, GameTask::cancel);

        Utils.clearCollection(chains);

        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
    }
}
