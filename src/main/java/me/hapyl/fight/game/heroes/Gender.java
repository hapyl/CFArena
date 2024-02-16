package me.hapyl.fight.game.heroes;

import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public enum Gender implements Described {

    MALE("&b♂ Male"),
    FEMALE("&d♀ Female"),
    UNKNOWN("&8❓ Uknown"),
    ALIEN("&2\uD83D\uDC7D Alien"),

    ;

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "No.";
    }

    @Override
    public String toString() {
        return name;
    }
}
