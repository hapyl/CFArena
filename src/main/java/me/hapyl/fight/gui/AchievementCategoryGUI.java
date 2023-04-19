package me.hapyl.fight.gui;

import me.hapyl.fight.game.achievement.Category;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;

public class AchievementCategoryGUI extends PlayerGUI {

    private final Category category;

    public AchievementCategoryGUI(Player player, Category category) {
        super(player, "Achievements", 6);

        this.category = category;
        updateInventory();
    }

    private void updateInventory() {
        openInventory();
    }
}
