package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Display {

    private static final Location ZERO = BukkitUtils.defLocation(0, 64, 0);

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
        return BukkitUtils.newLocation(location);
    }

    @Nonnull
    public World getWorld() {
        return location.getWorld();
    }

    public <T> void particle0(@Nonnull Location location, @Nonnull Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed, @Nullable Predicate<Player> filter, @Nullable T data) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == this.player || (filter != null && filter.test(player))) {
                player.spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, speed, data);
            }
        }
    }

    public <T> void particle(@Nonnull Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed, @Nonnull T data) {
        particle0(location, particle, amount, offsetX, offsetY, offsetZ, speed, null, data);
    }

    public <T> void particle(@Nonnull Particle particle, int amount, float speed, @Nonnull T data) {
        particle0(location, particle, amount, 0.0d, 0.0d, 0.0d, speed, null, data);
    }

    public void particle(@Nonnull Particle particle, int amount, float speed) {
        particle0(location, particle, amount, 0.0d, 0.0d, 0.0d, speed, null, null);
    }

    public void particle(@Nonnull Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed) {
        particle0(location, particle, amount, offsetX, offsetY, offsetZ, speed, null, null);
    }

    public void particle(@Nonnull Location location, @Nonnull Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed) {
        particle0(location, particle, amount, offsetX, offsetY, offsetZ, speed, null, null);
    }

    public void sound(@Nonnull Location location, @Nonnull Sound sound, float pitch) {
        PlayerLib.playSound(location, sound, pitch);
    }

    public void sound(@Nonnull Sound sound, float pitch) {
        sound(this.location, sound, pitch);
    }

    public void repeat(int maxTicks, int period, @Nonnull BiConsumer<Runnable, Integer> consumer) {
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

    @Nonnull
    public Item dropItem(@Nonnull Material material, int lifeTicks) {
        final Item item = location.getWorld().dropItemNaturally(
                location,
                new ItemBuilder(material).setName(String.valueOf(ThreadLocalRandom.current().nextFloat())).toItemStack()
        );

        SynchronizedGarbageEntityCollector.add(item);

        item.setPickupDelay(Short.MAX_VALUE);
        item.setTicksLived(Math.max(6000 - lifeTicks, 1));

        return item;
    }

    @Nonnull
    public String getName() {
        return player == null ? "Someone" : player.getName();
    }

    @Nonnull
    public List<Player> getPlayersWhoCanSeeContrail() {
        final List<Player> list = Lists.newArrayList();

        if (this.player == null) {
            return list;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == this.player || EnumSetting.SEE_OTHERS_CONTRAIL.isEnabled(player)) {
                list.add(player);
            }
        }

        return list;
    }

    @Nonnull
    public Location getPlayerLocationOrZero() {
        return player != null ? player.getLocation() : ZERO;
    }

    public void offset(double x, double y, double z, @Nonnull Consumer<Location> consumer) {
        location.add(x, y, z);
        consumer.accept(location);
        location.subtract(x, y, z);
    }
}
