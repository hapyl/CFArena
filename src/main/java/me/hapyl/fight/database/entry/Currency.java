package me.hapyl.fight.database.entry;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.FormattedEnum;

import javax.annotation.Nonnull;

public enum Currency implements FormattedEnum {
    COINS(new Color("#FFD700"), "🪙", "Coins"),
    RUBIES(new Color("#9B111E"), "💎", "Rubies"),
    CHEST_DUST(new Color("#964B00"), "📦", "Dust"),
    ACHIEVEMENT_POINT(Color.ROYAL_BLUE, "\uD83C\uDF1F", "Achievement Points"),

    ;

    private final Color color;
    private final String prefix;
    private final String name;

    Currency(Color color, String prefix, String name) {
        this.color = color;
        this.prefix = prefix;
        this.name = name;
    }

    public String getPath() {
        return name().toLowerCase();
    }

    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }
}
