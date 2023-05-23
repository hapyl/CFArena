package me.hapyl.fight.game.talents.storage.moonwalker;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class MoonsliteZone extends Talent {

    @DisplayField private final double radius = 3.0d;
    @DisplayField private final double radiusY = 7.0d;

    public MoonsliteZone() {
        super("yes", "no", Material.PURPLE_DYE);

        setDuration(100);
    }

    @Override
    public Response execute(Player player) {
        final Block block = player.getTargetBlockExact(10);

        if (block == null) {
            return Response.error("No valid block in sight!");
        }

        final Location center = block.getLocation().add(0.5d, 0.5d, 0.5d);
        final Location location = center.clone();

        new GameTask() {
            private int tick = getDuration();
            private double theta = 0.0d;
            private double y = 0.0d;

            @Override
            public void run() {
                if (tick-- < 0) {
                    this.cancel();

                    // Push down
                    getInRange(center, 1.0d).forEach(entity -> {
                        if (entity == player) {
                            return;
                        }

                        final Vector velocity = entity.getVelocity();

                        entity.setVelocity(new Vector(velocity.getX() / 2, -2.0d, velocity.getZ() / 2));
                    });

                    return;
                }

                // Pull Up
                getInRange(center).forEach(entity -> {
                    if (entity == player) {
                        return;
                    }

                    final Vector velocity = entity.getVelocity();

                    entity.setVelocity(new Vector(velocity.getX() / 2, 0.3d, velocity.getZ() / 2));
                });

                // Fx
                modifyLocationAnd(location, Math.sin(theta) * radius, y, Math.cos(theta) * radius, loc -> {
                    PlayerLib.spawnParticle(location, Particle.SPELL_WITCH, 5);
                });

                // Fx backwards
                modifyLocationAnd(location, Math.cos(theta) * radius, y, Math.sin(theta) * radius, loc -> {
                    PlayerLib.spawnParticle(location, Particle.CRIT, 5);
                });

                // Progress y and theta
                y += radiusY / getDuration();
                theta = (theta >= Math.PI * 2) ? 0 : theta + (Math.PI / 16);
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    public void repeat(Consumer<Integer> runnable, int delay, int max) {
        GameTask.runTaskTimerTimes((task, i) -> runnable.accept(i), 0, delay, max);
    }

    public void modifyLocationAnd(Location location, double x, double y, double z, Consumer<Location> andThen) {
        location.add(x, y, z);
        andThen.accept(location);
        location.subtract(x, y, z);
    }

    @Nonnull
    public Collection<LivingEntity> getInRange(@Nonnull Location location) {
        return getInRange(location, 0.0d);
    }

    public Collection<LivingEntity> getInRange(@Nonnull Location location, double y) {
        final World world = location.getWorld();
        final List<LivingEntity> list = Lists.newArrayList();

        if (world == null) {
            return list;
        }

        world.getNearbyEntities(location, radius, radiusY + y, radius).forEach(entity -> {
            if (Utils.isEntityValid(entity) && entity instanceof LivingEntity living) {
                list.add(living);
            }
        });

        return list;
    }

}
