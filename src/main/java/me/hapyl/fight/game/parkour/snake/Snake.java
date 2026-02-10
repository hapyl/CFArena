package me.hapyl.fight.game.parkour.snake;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.packet.PacketBlockDisplay;
import me.hapyl.eterna.module.entity.packet.PacketEntity;
import me.hapyl.eterna.module.util.Buffer;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Direction;
import me.hapyl.fight.CF;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Snake extends BukkitRunnable {

    public static final Matrix4f MATRIX = CFUtils.parseMatrix(
            0.5000f, 0.0000f, 0.0000f, 0.2500f,
            0.0000f, 0.5000f, 0.0000f, 0.2500f,
            0.0000f, 0.0000f, 0.5000f, -0.7500f,
            0.0000f, 0.0000f, 0.0000f, 1.0000f
    );

    protected final Set<PacketEntity<?>> entities;
    protected final LinkedList<Location> locations;

    private Material material;
    private BlockData blockData;

    private int period;
    private int length;
    private double radius;
    private Iterator<Location> iterator;
    private Buffer<Location> buffer;

    private Snake() {
        this.locations = Lists.newLinkedList();
        this.entities = Sets.newHashSet();
        this.radius = 32;
        this.period = 10;
        this.length = 5;
        this.material = Material.STONE;
        this.blockData = Material.STONE.createBlockData();
    }

    public void deleteEntities() {
        entities.forEach(PacketEntity::destroy);
        entities.clear();
    }

    public void createEntities() {
        deleteEntities();
        final BlockData blockData = material.createBlockData();

        for (Location location : locations) {
            // Use packet entities
            final PacketBlockDisplay entity = new PacketBlockDisplay(location);
            entity.setBlockData(blockData);
            entity.setTransformation(MATRIX);

            this.entities.add(entity);
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

                // Fx
                location.getWorld().spawnParticle(Particle.BLOCK, location, 3, blockData);
            }
        };

        this.runTaskTimer(CF.getPlugin(), 0, period);
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

        final Location location = iterator.next();

        location.getBlock().setType(material, false);
        location.getWorld().playSound(location, Sound.ENTITY_SILVERFISH_STEP, 1.0f, 2.0f);

        buffer.add(location);
    }

    public boolean isStarted() {
        return buffer != null;
    }

    public void stop() {
        cancel();
        deleteEntities();
    }

    @Nonnull
    public List<Location> getLocations() {
        return locations;
    }

    private boolean anyPlayersNearby(double radius) {
        final Location startLocation = getStart();
        final World world = startLocation.getWorld();

        if (world == null) {
            return false;
        }

        return !world.getNearbyEntities(startLocation, radius, radius, radius, entity -> entity instanceof Player).isEmpty();
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
            snake.blockData = material.createBlockData();
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

            final int[] values = direction.getOffset();
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
