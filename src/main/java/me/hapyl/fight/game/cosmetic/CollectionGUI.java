package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.experience.ExperienceGUI;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CollectionGUI extends PlayerGUI {
    public CollectionGUI(Player player) {
        super(player, "Collection", 5);
        setOpenEvent(e -> {
            PlayerLib.playSound(player, Sound.BLOCK_CHEST_OPEN, 1.0f);
        });

        update();
        openInventory();
    }

    public void update() {
        setItem(25,
                ItemBuilder.of(Material.EMERALD, "Experience")
                        .addSmartLore("Earn experience in game and unlock unique reward!")
                        .addLore()
                        .addLore("&eClick to open Experience Menu!")
                        .asIcon(), ExperienceGUI::new
        );
    }
}
