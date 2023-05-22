package me.hapyl.fight.game.maps.healthpack;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.reflect.Ticking;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class ActivePack implements Ticking {

    private final GamePack pack;
    private final Location location;

    private ArmorStand entity;

    public ActivePack(GamePack pack, Location location) {
        this.pack = pack;
        this.location = location;

        next(true);
    }

    public void next() {
        this.next(false);
    }

    @Nullable
    public ArmorStand getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void tick() {
        if (entity == null) {
            return;
        }

        final Location entityLocation = entity.getLocation();

        entityLocation.setYaw(entityLocation.getYaw() + 5);
        entity.teleport(entityLocation);

        entityLocation.add(0.0d, 1.0d, 0.0d); // add for particle

        pack.displayParticle(entityLocation);
    }

    public final void pickup0(Player player) {
        pack.onPickup(player);
        next();
    }

    private void next(boolean start) {
        new GameTask() {
            @Override
            public void run() {
                createEntity();
            }
        }.runTaskLater(start ? pack.getSpawnPeriod() / 2 : pack.getSpawnPeriod());
    }

    public void createEntity() {
        if (entity != null) {
            entity.remove();
            entity = null;
        }

        entity = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setSmall(true);
            self.setVisible(false);
            Utils.setEquipment(self, equipment -> equipment.setHelmet(pack.getTexture()));
        });
    }

}
