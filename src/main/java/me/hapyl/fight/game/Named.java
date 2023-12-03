package me.hapyl.fight.game;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An enum collection of named objects, such as abilities etc.
 */
public enum Named {

    BERSERK("💢", "Berserk", ChatColor.DARK_RED),
    ;

    private final String character;
    private final String value;
    private final String color;

    Named(String character, String value, ChatColor... colors) {
        this.character = character;
        this.value = value;
        this.color = getColor(colors);
    }

    @Nonnull
    public String getCharacter() {
        return character;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    @Nonnull
    public String getColor() {
        return color;
    }

    @Nonnull
    public String toString(@Nullable String prefix, @Nullable String suffix) {
        return prefix + this + suffix;
    }

    @Override
    public String toString() {
        return color + character + " " + color + value + "&7";
    }

    private static String getColor(ChatColor[] colors) {
        if (colors == null || colors.length == 0) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();

        for (ChatColor color : colors) {
            builder.append(color.toString());
        }

        return builder.toString();
    }
}
