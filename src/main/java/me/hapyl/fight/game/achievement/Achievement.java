package me.hapyl.fight.game.achievement;

import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

/**
 * Base achievement class.
 */
public class Achievement {

    private final String name;
    private final String description;

    private Category category;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.category = Category.GAMEPLAY;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void displayComplete(Player player) {
        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, "&6&lACHIEVEMENT COMPLETE");
        Chat.sendCenterMessage(player, "&a" + getName());
        Chat.sendMessage(player, "");
    }

    public void onComplete(Player player) {
    }
}
