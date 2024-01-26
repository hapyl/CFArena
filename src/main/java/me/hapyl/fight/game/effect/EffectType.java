package me.hapyl.fight.game.effect;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum EffectType {

    NEUTRAL("Neutral", ChatColor.YELLOW),
    POSITIVE("Positive", ChatColor.GREEN),
    NEGATIVE("Negative", ChatColor.RED);

    private final String name;
    private final ChatColor color;

    EffectType(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public ChatColor getColor() {
        return color;
    }
}
