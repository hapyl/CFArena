package me.hapyl.fight.game.parkour.snake;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.maps.features.Direction;
import me.hapyl.fight.util.Buffer;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;

public class Snake extends BukkitRunnable {

    public static final Transformation TRANSFORMATION = new Transformation(
            new Vector3f(0.125f, 0.125f, 0.125f),
            new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f),
            new Vector3f(0.25f, 0.25f, 0.25f),
            new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f)
    );

    private final LinkedList<Location> locations;
    private Material material;
    private int period;
    private int length;
    private double radius;
    private Iterator<Location> iterator;
    private Buffer<Location> buffer;

    private Snake() {
        this.locations = Lists.newLinkedList();
        this.radius = 32;
        this.period = 10;
        this.length = 5;
        this.material = Material.STONE;
    }

    public void deleteEntities() {
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }

            if (entity.getScoreboardTags().contains("SnakeEntity")) {
                entity.remove();
            }
        }
    }

    public void createEntities() {
        deleteEntities();

        // FIXME (hapyl): 003, Jun 3: Switch to BlockDisplay maybe, I can't fucking center the block, is it a div or something

        final ItemStack itemStack = new ItemStack(material);

        for (Location location : locations) {
            Entities.ARMOR_STAND_MARKER.spawn(location.clone().subtract(0.0d, 1.0d, 0.0d), self -> {
                final EntityEquipment equipment = self.getEquipment();
                if (equipment != null) {
                    equipment.setHelmet(itemStack);
                }

                self.setSmall(true);
                self.setInvisible(true);
                self.addScoreboardTag("SnakeEntity");
            });
        }
    }

    @Nonnull
    public Location getStart() {
        final Location first = locations.peek();

        if (first == null) {
            throw new IllegalStateException("illegal snake creation");
        }

        return first;
    }

    public void start() {
        if (isStarted()) {
            return;
        }

        iterator = locations.iterator();
        buffer = new Buffer<>(length) {
            @Override
            public void unbuffered(@Nonnull Location location) {
                location.getBlock().setType(Material.AIR, false);
            }
        };

        this.runTaskTimer(Main.getPlugin(), 0, period);
    }

    @Override
    public void run() {
        // Skip iteration when there are no nearby players
        if (!anyPlayersNearby(radius)) {
            return;
        }

        if (!iterator.hasNext()) {
            iterator = locations.iterator();
        }

        final Location next = iterator.next();
        next.getBlock().setType(material, false);
        buffer.add(next);
    }

    public boolean isStarted() {
        return buffer != null;
    }

    public void stop() {
        cancel();
        deleteEntities();
    }

    private boolean anyPlayersNearby(double radius) {
        final Location startLocation = getStart();
        final World world = startLocation.getWorld();

        if (world == null) {
            return false;
        }

        return world.getNearbyEntities(startLocation, radius, radius, radius, entity -> entity instanceof Player).size() > 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Snake snake;

        public Builder() {
            snake = new Snake();
        }

        public Builder setBlock(Material material) {
            if (!material.isBlock()) {
                throw new IllegalArgumentException("material must be a block, not " + material);
            }

            snake.material = material;
            return this;
        }

        public Builder setPeriod(int period) {
            snake.period = Math.max(1, period);
            return this;
        }

        public Builder setRadius(double radius) {
            snake.radius = Math.max(16, radius);
            return this;
        }

        public Builder setLength(int length) {
            snake.length = Math.max(length, 1);
            return this;
        }

        public Builder next(int x, int y, int z) {
            snake.locations.add(BukkitUtils.defLocation(x, y, z));
            return this;
        }

        public Builder next(@Nonnull Location location) {
            snake.locations.add(location);
            return this;
        }

        public Builder next(@Nonnull Direction direction) {
            if (direction == Direction.NONE) {
                throw new IllegalArgumentException("direction cannot be null");
            }

            final Location last = snake.locations.peekLast();
            if (last == null) {
                throw new IllegalStateException("location not initiated");
            }

            final int[] values = direction.getValues();
            final Location nextLocation = BukkitUtils.newLocation(last).add(values[0], values[1], values[2]);

            snake.locations.add(nextLocation);
            return this;
        }

        public Snake end(int x, int y, int z) {
            next(x, y, z);
            return snake;
        }

    }

}
