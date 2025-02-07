package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class GiantSwordCosmetic extends Cosmetic {
    public GiantSwordCosmetic(@Nonnull Key key) {
        super(key, "Giant Sword", Type.KILL);

        setDescription("""
                A big R.I.P for the fallen warrior.
                """
        );

        setRarity(Rarity.LEGENDARY);
        setIcon(Material.IRON_SWORD);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
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

            EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
        });

        Nulls.runIfNotNull(location.getWorld(), world -> {
            world.playSound(location, Sound.BLOCK_ANVIL_LAND, 50, 0.5f);
        });

        GameTask.runLater(giant::remove, 60);
    }
}
