package me.hapyl.fight.game.shop;

import org.bukkit.ChatColor;

public enum Rarity {

    UNSET(ChatColor.DARK_RED, "set rarity you idiot", -1),
    COMMON(ChatColor.GRAY, "Common", 100),
    UNCOMMON(ChatColor.GREEN, "Uncommon", 200),
    RARE(ChatColor.BLUE, "Rare", 400),
    EPIC(ChatColor.DARK_PURPLE, "Epic", 1000),
    LEGENDARY(ChatColor.GOLD, "Legendary", 5000),
    MYTHIC(ChatColor.LIGHT_PURPLE, "Mythic", 10000);

    private final ChatColor color;
    private final String name;
    private final long defaultPrice;

    Rarity(ChatColor color, String name, long defaultPrice) {
        this.color = color;
        this.name = name;
        this.defaultPrice = defaultPrice;
    }

    public long getDefaultPrice() {
        return defaultPrice;
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
