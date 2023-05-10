package me.hapyl.fight.gui;

import me.hapyl.fight.game.achievement.Category;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerAutoGUI;
import org.bukkit.entity.Player;

public class AchievementGUI extends PlayerAutoGUI {
    public AchievementGUI(Player player) {
        super(player, "Achievements", 5);
        updateInventory();
    }

    private void updateInventory() {

        for (Category value : Category.values()) {
            addItem(new ItemBuilder(value.getMaterial()).setName(value.getName()).addLore(value.getDescription()).asIcon(), player -> {
                new AchievementCategoryGUI(player, value);
            });
        }

        openInventory();
    }
}
