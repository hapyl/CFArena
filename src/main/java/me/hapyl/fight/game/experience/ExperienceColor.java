package me.hapyl.fight.game.experience;

import me.hapyl.spigotutils.module.chat.Chat;

import javax.annotation.Nonnull;

public enum ExperienceColor {

    GRAY("&7"),
    WHITE("&f"),
    GREEN("&a"),
    DARK_GREEN("&2"),
    AQUA("&b"),
    BLUE("&9"),
    YELLOW("&e"),
    GOLDEN("&6"),
    RED("&c"),
    DARK_RED("&4"),
    BOLD_DARK_RED("&4&l");

    private final String color;
    private final String name;

    ExperienceColor(String color) {
        this.color = color;
        this.name = Chat.capitalize(this);
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return color + name;
    }

    @Nonnull
    public static ExperienceColor getByLevel(long level) {
        return values()[Math.min((int) (level / 5), values().length - 1)];
    }
}
