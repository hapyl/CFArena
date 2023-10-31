package me.hapyl.fight.game;

import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An enum collection of named objects, such as abilities etc.
 */
public enum Named {

    BERSERK("💢", "Berserk", Color.DARK_RED),
    CURSE_OF_GREED("\uD83E\uDDFF", "Curse of Greed", Color.GOLD),
    ;

    private final String character;
    private final String name;
    private final Color color;

    Named(@Nonnull String character, @Nonnull String name, @Nonnull Color color) {
        this.character = character;
        this.color = color;
        this.name = colorName(name);
    }

    @Nonnull
    public String getCharacter() {
        return character;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Color getColor() {
        return color;
    }

    @Nonnull
    public String toString(@Nullable String prefix, @Nullable String suffix) {
        return prefix + this + suffix;
    }

    @Override
    public String toString() {
        return color + character + " " + color + name + "&7";
    }

    private String colorName(String name) {
        final String[] strings = name.split(" ");
        final StringBuilder builder = new StringBuilder();

        for (String string : strings) {
            builder.append(color).append(string).append(" ");
        }

        return builder.toString().trim();
    }

}
