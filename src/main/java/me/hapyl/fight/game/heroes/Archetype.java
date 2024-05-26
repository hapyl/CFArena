package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Prefixed;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public enum Archetype implements Prefixed {

    DAMAGE(Material.BLAZE_POWDER, "&4&l💢&4", "Damage", "Experts in dealing as much damage as possible."),
    RANGE(Material.BOW, "&b&l🎯&b", "Range", "Rangers are dead-eye shooters that can hold distance to strike."),
    MOBILITY(Material.RABBIT_FOOT, "&d👣", "Mobility", "Fast and mobile, they zip around the battlefield."),
    STRATEGY(Material.LIGHT, "&e💡", "Strategy", "Strategists rely on their talents, rather than strength to win."),
    SUPPORT(Material.GOLDEN_APPLE, "&2🍀", "Support", "Applies buffs to self and allies and keeps them alive."),
    HEXBANE(
            Material.NETHERITE_SCRAP,
            Color.HEXBANE + "🕷",
            "Hexbane",
            "Masters of debuffs, they weaken and hinder enemies with eerie precision."
    ),

    NOT_SET;

    private final Material material;
    private final String prefix;
    private final String name;
    private final String description;

    Archetype() {
        this(Material.BEDROCK, "", "", "");
    }

    Archetype(@Nonnull Material material, @Nonnull String prefix, @Nonnull String name, @Nonnull String description) {
        this.material = material;
        this.prefix = prefix;
        this.name = name;
        this.description = description;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
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
