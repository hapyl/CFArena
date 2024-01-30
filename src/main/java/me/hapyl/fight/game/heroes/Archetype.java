package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Prefixed;

import javax.annotation.Nonnull;

public enum Archetype implements Prefixed {

    DAMAGE("&4&l💢&4", "Damage", "Experts in dealing as much damage as possible."),
    RANGE("&b&l🎯&b", "Range", "Rangers are dead-eye shooters that can hold distance to strike."),
    MAGIC("&5🌟", "Magic", "Experts in casting magic spells."),
    DEFENSE("&3🛡", "Defense", "Provides defense for self and allies."),
    MOBILITY("&d👣", "Mobility", "Fast and mobile, they zip around the battlefield."),
    STRATEGY("&e💡", "Strategy", "Strategists rely on their abilities, rather than strength to win."),
    SUPPORT("&2🍀", "Support", "Provide buffs to self and allies."),
    HEXBANE(
            new Color("#5C3D2E") + "🕷",
            "Hexbane",
            "Masters of debuffs, they weaken and hinder enemies with eerie precision."
    ),

    NOT_SET();

    private final String prefix;
    private final String name;
    private final String description;

    Archetype() {
        this("", "", "");
    }

    Archetype(@Nonnull String prefix, @Nonnull String name, @Nonnull String description) {
        this.prefix = prefix;
        this.name = name;
        this.description = description;
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return prefix + " " + name;
    }
}
