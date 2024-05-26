package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Described;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum TalentType implements Described {
    // FIXME (hapyl): 027, Mar 27: ehhh like rename or add better description like DEFENSE doesn't have to give defense, it can be like def increase or jump health IDK!
    DAMAGE(
            "Damage",
            "Deals damage to enemies.",
            Color.RED
    ),
    ENHANCE(
            "Enhance",
            "Strengthen yourself for the battle.",
            Color.DARK_AQUA
    ),
    SUPPORT(
            "Support",
            "Provide buffs to self or teammates.",
            Color.GREEN
    ),
    IMPAIR(
            "Impair",
            "Weaken enemies by debuffing them.",
            Color.HEXBANE
    ),
    MOVEMENT(
            "Movement",
            "Provides a way to swiftly flee the battlefield or enter the battle. Or just to have fun.",
            Color.AQUA
    ),
    DEFENSE(
            "Defense",
            "Provides shields for self or teammates.",
            Color.DARK_GREEN
    );

    private final String name;
    private final String description;
    private final Color color;

    TalentType(String name, String description, Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
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
        return color + name + Color.GRAY;
    }
}
