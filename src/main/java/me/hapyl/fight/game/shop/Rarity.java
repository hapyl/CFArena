package me.hapyl.fight.game.shop;

import org.bukkit.ChatColor;

public enum Rarity {

    UNSET(ChatColor.DARK_RED, "set rarity you idiot"),
    COMMON(ChatColor.GRAY, "Common"),
    UNCOMMON(ChatColor.GREEN, "Uncommon"),
    RARE(ChatColor.BLUE, "Rare"),
    EPIC(ChatColor.DARK_PURPLE, "Epic"),
    LEGENDARY(ChatColor.GOLD, "Legendary"),
    MYTHIC(ChatColor.LIGHT_PURPLE, "Mythic");

    private final ChatColor color;
    private final String name;

    Rarity(ChatColor color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getNameColored() {
        return color + name;
    }
}
