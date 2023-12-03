package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HealthPackPlatform {

    public HealthPackPlatform(GamePack pack) {
        pack.getLocations().forEach(this::create);
    }

    private void create(Location location) {
        Entities.ARMOR_STAND_MARKER.spawn(location.clone().subtract(0.0d, 1.6d, 0.0d), self -> {
            Utils.setEquipment(self, equipment -> {
                self.setVisible(false);
                equipment.setHelmet(new ItemStack(Material.SMOOTH_STONE_SLAB));
            });
        });
    }

}
