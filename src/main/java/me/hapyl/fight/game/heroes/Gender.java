package me.hapyl.fight.game.heroes;

import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

// TODO (hapyl): 019, Feb 19: Maybe rename this because gender is kinda weird 
public enum Gender implements Described {

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
