package me.hapyl.fight.game.heroes;

import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public enum Race implements Described {

    HUMAN("&f🧑 &fHuman"),
    ALIEN("&a👽 &fAlien"),
    CYBERNETIC("&b🤖 &fCybernetic"),
    VAMPIRE("&4🦇 &fVampire"),

    UNKNOWN("&8❓ Unknown"),

    ;

    private final String name;

    Race(String name) {
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
        return name;
    }
}
