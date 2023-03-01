package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;

public class GiantSwordCosmetic extends Cosmetic {
    public GiantSwordCosmetic() {
        super("Giant Sword", "A big R.I.P for the fallen warrior.", 5000, Type.KILL, Rarity.LEGENDARY);
    }

    @Override
    public void onDisplay(Display display) {
        final Location location = display.getLocation().subtract(1.6d, 0.6d, 4.3d);

        final Giant giant = Entities.GIANT.spawn(location, self -> {
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setCustomNameVisible(false);
            self.setCustomName("Dinnerbone");
            self.setAI(false);
            self.setGravity(false);

            Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                equipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            });
        });

        Nulls.runIfNotNull(location.getWorld(), world -> {
            world.playSound(location, Sound.BLOCK_ANVIL_LAND, 50, 0.5f);
        });

        GameTask.runLater(giant::remove, 60);
    }
}
