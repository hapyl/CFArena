package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Display {

    private final Player player;
    private final Location location;

    public Display(@Nullable Player player, @Nonnull Location location) {
        this.player = player;
        this.location = location.clone();
        this.location.add(0.0d, player == null ? 0.5d : player.getEyeHeight() / 2, 0.0d);
        this.location.setYaw(0.0f);
        this.location.setPitch(0.0f);
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public Location getLocation() {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    @Nonnull
    public World getWorld() {
        if (location.getWorld() == null) {
            throw new IllegalStateException("Location's world is null!");
        }
        return location.getWorld();
    }

    private <T> void particle0(Location location, Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed, @Nullable T data, @Nullable Player... force) {
        particle0(location, particle, amount, offsetX, offsetY, offsetZ, speed, null, data, force);
    }

    public <T> void particle0(Location location, Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed, @Nullable Predicate<Player> filter, @Nullable T data, @Nullable Player... force) {
        // Force players are NOT checked for visibility
        if (force != null) {
            for (Player player : force) {
                if (data != null) {
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, data);
                }
                else {
                    player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != this.player && (filter != null && filter.test(player))) {
                continue;
            }

            if (data != null) {
                player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, data);
            }
            else {
                player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed);
            }
        }
    }

    @SuppressWarnings("all")
    public <T> void particle(Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed, T data) {
        particle0(getLocation(), particle, amount, offsetX, offsetY, offsetZ, speed, data);
    }

    @SuppressWarnings("all")
    public <T> void particle(Particle particle, int amount, float speed, T data) {
        particle(particle, amount, 0.0d, 0.0d, 0.0d, speed, data);
    }

    public void particle(Particle particle, int amount, float speed) {
        particle0(getLocation(), particle, amount, 0.0d, 0.0d, 0.0d, speed, null);
    }

    public void particle(Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed) {
        particle0(getLocation(), particle, amount, offsetX, offsetY, offsetZ, speed, null);
    }

    public void particle(Location location, Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed) {
        particle0(location, particle, amount, offsetX, offsetY, offsetZ, speed, null);
    }

    public void sound(Sound sound, float pitch) {
        PlayerLib.playSound(location, sound, pitch);
    }

    public void repeat(int maxTicks, int period, BiConsumer<Runnable, Integer> consumer) {
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick++ >= maxTicks) {
                    this.cancel();
                    return;
                }

                consumer.accept(this, tick);
            }
        }.runTaskTimer(0, period);
    }

    public Item item(Material material, int lifeTicks) {
        if (location.getWorld() == null) {
            throw new IllegalStateException("Location's world is null!");
        }

        final Item item = location.getWorld()
                .dropItemNaturally(location, new ItemBuilder(material).setName(String.valueOf(ThreadRandom.nextFloat())).toItemStack());

        Entities.getEntities().add(item);

        item.setPickupDelay(Short.MAX_VALUE);
        item.setTicksLived(Math.max(6000 - lifeTicks, 1));

        return item;
    }

    public String getName() {
        return player == null ? "Someone" : player.getName();
    }

    public List<Player> getPlayersWhoCanSeeContrail() {
        final List<Player> list = Lists.newArrayList();

        if (this.player == null) {
            return list;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == this.player || Setting.SEE_OTHERS_CONTRAIL.isEnabled(player)) {
                list.add(player);
            }
        }

        return list;
    }
}
