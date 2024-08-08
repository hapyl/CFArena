package me.hapyl.fight.game.talents.juju;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Range;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Queue;

// Using armor stands for now because Item Displays are the bane of my existence.
public abstract class Orbiting extends TickingGameTask implements Iterable<ItemDisplay> {

    public static final Matrix4f DEFAULT_MATRIX = new Matrix4f(
            1.0000f,
            0.0000f,
            0.0000f,
            0.0000f,
            0.0000f,
            1.0000f,
            0.0000f,
            0.0000f,
            0.0000f,
            0.0000f,
            1.0000f,
            0.0000f,
            0.0000f,
            0.0000f,
            0.0000f,
            1.0000f
    );

    private final Queue<ItemDisplay> orbiting;
    private final int capacity;
    private final ItemStack itemStack;
    private final double[] offset;

    private double theta = 0.0d;
    private double speed = Math.PI / 20;
    private double distance = 1.25d;
    private ItemDisplay.ItemDisplayTransform transform;
    private Matrix4f matrix4f;

    public Orbiting(int capacity, @Nonnull Material material) {
        if (!material.isItem()) {
            throw new IllegalArgumentException("material must be an item, %s is not!".formatted(material));
        }

        this.orbiting = Lists.newLinkedList();
        this.capacity = capacity;
        this.itemStack = new ItemStack(material);
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
        this.matrix4f = DEFAULT_MATRIX;
        this.offset = new double[3];
    }

    public void setOffset(double x, double y, double z) {
        this.offset[0] = x;
        this.offset[1] = y;
        this.offset[2] = z;
    }

    public double[] getOffset() {
        return offset;
    }

    public double getSpeed() {
        return speed;
    }

    public Orbiting setSpeed(double speed) {
        this.speed = Math.max(speed, 0);
        return this;
    }

    public Orbiting setMatrix(Matrix4f matrix) {
        this.matrix4f = matrix;
        return this;
    }

    public Orbiting setMatrix(@Range(min = 16, max = 16) float... matrix) {
        return setMatrix(CFUtils.parseMatrix(matrix));
    }

    public Orbiting setTransform(ItemDisplay.ItemDisplayTransform transform) {
        this.transform = transform;
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public Orbiting setDistance(double distance) {
        this.distance = Math.max(distance, 0);
        return this;
    }

    public void transform(@Nonnull Matrix4f matrix) {
        orbiting.forEach(display -> display.setTransformationMatrix(matrix));
    }

    public int size() {
        return orbiting.size();
    }

    public int count() {
        return size();
    }

    public void addMissing(@Nonnull Location location) {
        for (int i = 0; i < capacity - orbiting.size() + 1; i++) {
            add(location);
        }
    }

    @Nonnull
    public ItemDisplay add(@Nonnull Location location) {
        ItemDisplay entity = null;

        if (orbiting.size() >= capacity) {
            entity = orbiting.peek();
        }

        if (entity == null) {
            entity = Entities.ITEM_DISPLAY.spawn(location, self -> {
                self.setTeleportDuration(1);
                self.setItemStack(itemStack);
                self.setTransformationMatrix(matrix4f);
                self.setItemDisplayTransform(transform);
            });

            onCreate(entity);
            orbiting.offer(entity);
        }

        return entity;
    }

    @Nullable
    public ItemDisplay remove() {
        final ItemDisplay polled = orbiting.poll();

        if (polled != null) {
            onRemove(polled);
        }

        return polled;
    }

    public void onCreate(@Nonnull ItemDisplay display) {
    }

    public void onOrbit(@Nonnull ItemDisplay display, @Nonnull Location location) {
        display.teleport(location);
    }

    public void onRemove(@Nonnull ItemDisplay display) {
        display.remove();
    }

    public boolean hasFirst() {
        return orbiting.peek() != null;
    }

    public void removeAll() {
        orbiting.forEach(Entity::remove);
        orbiting.clear();
    }

    @Override
    public Iterator<ItemDisplay> iterator() {
        return orbiting.iterator();
    }

    /**
     * Gets the anchor location to orbit around.
     */
    @Nonnull
    public abstract Location getAnchorLocation();

    @Override
    public final void run(int tick) {
        if (orbiting.isEmpty()) {
            cancel();
            return;
        }

        final Location location = getAnchorLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        final double offset = ((Math.PI * 2) / Math.max(size(), 1));

        int pos = 1;
        for (final ItemDisplay display : orbiting) {
            final double x = this.offset[0] + (distance * Math.sin(theta + offset * pos));
            final double y = this.offset[1] + 0;
            final double z = this.offset[2] + (distance * Math.cos(theta + offset * pos));

            location.add(x, y, z);
            onOrbit(display, location);
            location.subtract(x, y, z);
            ++pos;
        }

        theta += speed;
        if (theta >= Math.PI * 2) {
            theta = 0;
        }
    }

}
