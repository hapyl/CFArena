package me.hapyl.fight.game.talents.archive.juju;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

// FIXME (hapyl): 029, Jun 29: Maybe rework later Block Studio will add Item Displays in a bit
// Using armor stands for now because Item Displays are the bane of my existence.
public abstract class Orbiting extends TickingGameTask implements Iterable<ArmorStand> {

    private final LinkedList<ArmorStand> orbiting;
    private final int capacity;
    private final ItemStack itemStack;

    private HeldType heldType;
    private double theta = 0.0d;
    private double speed = Math.PI / 20;
    private double distance = 1.25d;

    public Orbiting(int capacity, @Nonnull Material material) {
        this.orbiting = Lists.newLinkedList();
        this.capacity = capacity;
        this.itemStack = new ItemStack(material);
        this.heldType = HeldType.RIGHT_HAND;
    }

    public double getSpeed() {
        return speed;
    }

    public Orbiting setSpeed(double speed) {
        this.speed = Math.max(speed, 0);
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public Orbiting setDistance(double distance) {
        this.distance = Math.max(distance, 0);
        return this;
    }

    public void rotate(double x, double y, double z) {
        orbiting.forEach(armorStand -> heldType.setItemRotation(armorStand, x, y, z));
    }

    public int size() {
        return orbiting.size();
    }

    public int count() {
        return size();
    }

    public Orbiting setHeldType(@Nonnull HeldType heldType) {
        this.heldType = heldType;
        return this;
    }

    public void addMissing(@Nonnull Location location) {
        for (int i = 0; i < capacity - orbiting.size(); i++) {
            add(location);
        }
    }

    @Nonnull
    public ArmorStand add(@Nonnull Location location) {
        ArmorStand entity = null;

        if (orbiting.size() >= capacity) {
            entity = orbiting.peekLast();
        }

        if (entity == null) {
            entity = Entities.ARMOR_STAND_MARKER.spawn(heldType.offsetLocation(location), self -> {
                heldType.setItem(Objects.requireNonNull(self.getEquipment()), itemStack);
                heldType.setItemRotation(self);

                self.setInvisible(true);
                self.setSilent(true);
            });

            orbiting.addLast(entity);
        }

        return entity;
    }

    @Nullable
    public ArmorStand remove() {
        return removeFirst();
    }

    @Nullable
    public ArmorStand removeFirst() {
        final ArmorStand polled = orbiting.pollFirst();

        if (polled != null) {
            onRemove(polled);
        }

        return polled;
    }

    public void onRemove(@Nonnull ArmorStand armorStand) {
        armorStand.remove();
    }

    @Nullable
    public ArmorStand removeLast() {
        final ArmorStand polled = orbiting.pollLast();

        if (polled != null) {
            onRemove(polled);
        }

        return polled;
    }

    public boolean hasFirst() {
        return orbiting.peekFirst() != null;
    }

    public boolean hasLast() {
        return orbiting.peekLast() != null;
    }

    public void removeAll() {
        orbiting.forEach(Entity::remove);
        orbiting.clear();
    }

    @Override
    public Iterator<ArmorStand> iterator() {
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
        for (final ArmorStand armorStand : orbiting) {
            final double x = (distance * Math.sin(theta + offset * pos));
            final double y = 0;
            final double z = (distance * Math.cos(theta + offset * pos));

            location.add(x, y, z);
            onOrbit(armorStand, location);
            location.subtract(x, y, z);
            ++pos;
        }

        theta += speed;
        if (theta >= Math.PI * 2) {
            theta = 0;
        }
    }

    public void onOrbit(@Nonnull ArmorStand armorStand, @Nonnull Location location) {
        armorStand.teleport(location);
    }

    public enum HeldType {
        HELMET,
        RIGHT_HAND(-0.05, 0.65, 0.35, 15, 45, 0) {
            @Override
            public void setItem(@Nonnull EntityEquipment equipment, @Nonnull ItemStack item) {
                equipment.setItemInMainHand(item);
            }

            @Override
            public void setItemRotation(@Nonnull ArmorStand armorStand, double x, double y, double z) {
                armorStand.setRightArmPose(toEuler(x, y, z));
            }
        },
        LEFT_HAND {
            @Override
            public void setItem(@Nonnull EntityEquipment equipment, @Nonnull ItemStack item) {
                equipment.setItemInOffHand(item);
            }

            @Override
            public void setItemRotation(@Nonnull ArmorStand armorStand, double x, double y, double z) {
                armorStand.setLeftArmPose(toEuler(x, y, z));
            }
        };

        private final double[] offset;
        private final double[] position;

        HeldType() {
            this(0, 0, 0, 0, 0, 0);
        }

        HeldType(double... values) {
            this.offset = new double[3];
            this.position = new double[3];

            System.arraycopy(values, 0, this.offset, 0, 3);
            System.arraycopy(values, 3, this.position, 0, 3);
        }

        public void setItem(@Nonnull EntityEquipment equipment, @Nonnull ItemStack item) {
            equipment.setHelmet(item);
        }

        public final void setItemRotation(@Nonnull ArmorStand armorStand) {
            setItemRotation(armorStand, position[0], position[1], position[2]);
        }

        public void setItemRotation(@Nonnull ArmorStand armorStand, double x, double y, double z) {
            armorStand.setHeadPose(toEuler(x, y, z));
        }

        @Nonnull
        public Location offsetLocation(@Nonnull Location location) {
            return BukkitUtils.newLocation(location).subtract(offset[0], offset[1], offset[2]);
        }

        protected EulerAngle toEuler() {
            return toEuler(position[0], position[1], position[2]);
        }

        protected EulerAngle toEuler(double x, double y, double z) {
            return new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        }

    }
}
