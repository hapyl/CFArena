package me.hapyl.fight.game.setting;

import me.hapyl.fight.util.Described;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Setting implements Described {

    private final Material material;
    private final String name;
    private final String description;
    private final Category category;
    private final boolean defaultValue;

    public Setting(Material material, String name, String description, Category category, boolean defaultValue) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultValue = defaultValue;
    }

    public Setting(Material material, String name, String description, Category category) {
        this(material, name, description, category, false);
    }

    public Material getMaterial() {
        return material;
    }

    public Category getCategory() {
        return category;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public void onEnable(@Nonnull Player player) {
        Chat.sendMessage(player, "&aYou turned %s on!", getName());
    }

    public void onDisabled(@Nonnull Player player) {
        Chat.sendMessage(player, "&cYou turned %s off!", getName());
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
}
