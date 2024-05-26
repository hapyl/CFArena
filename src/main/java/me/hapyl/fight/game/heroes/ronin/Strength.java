package me.hapyl.fight.game.heroes.ronin;

import org.bukkit.ChatColor;

public enum Strength {

    NORMAL(0, 1.0d, ChatColor.DARK_GREEN, ChatColor.GREEN),
    PERFECT(7, 1.5d, ChatColor.DARK_RED, ChatColor.RED),
    WEAK(9, 0.5d, ChatColor.GOLD, ChatColor.YELLOW);

    public final double startIndex;
    public final double multiplier;
    public final ChatColor color;
    public final ChatColor colorCurrent;

    Strength(double startIndex, double multiplier, ChatColor color, ChatColor colorCurrent) {
        this.startIndex = startIndex;
        this.multiplier = multiplier;
        this.color = color;
        this.colorCurrent = colorCurrent;
    }
}
