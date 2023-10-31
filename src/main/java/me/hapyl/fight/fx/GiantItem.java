package me.hapyl.fight.fx;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GiantItem {

    private static final double[] centerOffset = { 2.0d, -7.5d, -3.2d };
    private final Giant giant;
    private Location location;

    public GiantItem(@Nonnull Location location, @Nonnull Material material) {
        this.location = location;

        giant = Entities.GIANT.spawn(location.clone().add(centerOffset[0], centerOffset[1], centerOffset[2]), self -> {
            self.setInvisible(true);
            self.setInvulnerable(true);
            self.setSilent(true);
            self.setAI(false);
        });

        setItem(material);
        removeCollision();
    }

    public void remove() {
        giant.remove();
    }

    public void hide(Player player) {
        player.hideEntity(Main.getPlugin(), giant);
    }

    public void show(Player player) {
        player.showEntity(Main.getPlugin(), giant);
    }

    public void setFlipped(boolean flipped) {
        giant.setCustomName(flipped ? "Dinnerbone" : null);
    }

    public void removeCollision() {
        EntityUtils.setCollision(giant, EntityUtils.Collision.DENY);
    }

    public void teleport(Location location) {
        this.location = location;
        this.location.add(centerOffset[0], centerOffset[1], centerOffset[2]);

        giant.teleport(this.location);
    }

    @Nullable
    public ItemStack getItem() {
        return getEquipment().getItemInMainHand();
    }

    public void setItem(@Nonnull Material material) {
        Validate.isTrue(material.isItem(), "material must be an item");
        setItem(new ItemStack(material));
    }

    public void setItem(@Nullable ItemStack item) {
        getEquipment().setItemInMainHand(item);
    }

    @Nonnull
    public Location getLocation() {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void rotate(double degrees) {
        final Location location = getLocation();

        final double radians = Math.toRadians(degrees);

        final double x = Math.sin(radians) * 3;
        final double z = Math.cos(radians) * 3;

        location.add(x, 0, z);

        // Look at the center
        final Vector vector = this.location.toVector().subtract(location.toVector());
        location.setDirection(vector);

        giant.teleport(location);
    }

    @Nonnull
    private EntityEquipment getEquipment() {
        final EntityEquipment equipment = giant.getEquipment();

        if (equipment == null) {
            throw new NullPointerException("equipment is null somehow");
        }

        return equipment;
    }

}
