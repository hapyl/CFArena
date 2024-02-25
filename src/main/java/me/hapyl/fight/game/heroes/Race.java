package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Named;

import javax.annotation.Nonnull;

public enum Race implements Named {

    HUMAN(Color.SKIN + "🧑 Human"),
    ALIEN("&a👽 &2Alien"),
    CYBERNETIC("&b🤖 &3Cybernetic"),
    VAMPIRE("&4🦇 &cVampire"),

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

    @Override
    public String toString() {
        return name;
    }
}
