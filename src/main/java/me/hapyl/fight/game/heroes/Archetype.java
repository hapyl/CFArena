package me.hapyl.fight.game.heroes;

import javax.annotation.Nonnull;

public enum Archetype {

    DAMAGE("Damage", "Experts in dealing as much damage as possible."),
    RANGE("Range", "Rangers are dead-eye shooters that can hold distance to strike."),
    DEFENSE("Defense", "Provides defense for self and allies."),
    MOBILITY("Mobility", "Fast and mobile, they zip around the battlefield."),
    STRATEGY("Strategy", "Strategists rely on their abilities, rather than combat to win."),
    SUPPORT("Support", "Provide buffs to self and allies."),

    NOT_SET("not set", "no set");

    private final String name;
    private final String description;

    Archetype(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
