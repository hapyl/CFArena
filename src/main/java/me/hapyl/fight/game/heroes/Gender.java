package me.hapyl.fight.game.heroes;

import me.hapyl.fight.util.Named;

import javax.annotation.Nonnull;

public enum Gender implements Named {

    MALE("&b♂ Male"),
    FEMALE("&d♀ Female"),

    UNKNOWN("&8❓ Unknown"),

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

    @Override
    public String toString() {
        return name;
    }
}
