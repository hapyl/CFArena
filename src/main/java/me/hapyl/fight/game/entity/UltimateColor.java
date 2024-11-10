package me.hapyl.fight.game.entity;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum UltimateColor {
    PRIMARY(ChatColor.AQUA, ChatColor.LIGHT_PURPLE),
    SECONDARY(ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE);

    private final ChatColor[] colors;

    UltimateColor(ChatColor charged, ChatColor overcharged) {
        this.colors = new ChatColor[] { charged, overcharged };
    }

    @Nonnull
    public ChatColor getColor(boolean isOvercharged) {
        return colors[isOvercharged ? 1 : 0];
    }
}
