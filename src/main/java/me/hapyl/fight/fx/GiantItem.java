package me.hapyl.fight.fx;

import me.hapyl.fight.game.talents.Removable;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;

public class GiantItem implements Removable {

    private final Entity marker;
    private final Giant giant;

    public GiantItem(Location location, ItemStack item) {
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        this.marker = Entities.MARKER.spawn(location, self -> {
            self.setSilent(true);
            self.setGravity(false);
        });

        this.giant = Entities.GIANT.spawn(getGiantLocation(), self -> {
            self.setInvisible(true);
            self.setSilent(true);
            self.setGravity(false);
            self.setAI(false);
            self.getEquipment().setItemInMainHand(item);

            EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
        });

        rotate(0);
    }

    public void teleport(Location location) {
        marker.teleport(location);
        syncGiant();
    }

    public void rotate(float degrees) {
        final Location location = marker.getLocation();

        location.setYaw(degrees);
        marker.teleport(location);

        syncGiant();
    }

    @Override
    public void remove() {
        marker.remove();
        giant.remove();
    }

    public void setY(double y) {
        teleport(marker.getLocation().add(0, y, 0));
    }

    private void syncGiant() {
        giant.teleport(getGiantLocation());
    }

    private Location getGiantLocation() {
        Location location = marker.getLocation();

        location = LocationHelper.getBehind(location, 4.5d);
        location = LocationHelper.getToTheLeft(location, 1.85d);
        location.subtract(0, 9, 0);

        return location;
    }

}
