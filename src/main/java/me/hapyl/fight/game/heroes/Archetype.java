package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Prefixed;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Set;

public enum Archetype implements Prefixed {

    // keep names kinda short since detailed display stacks prefixes AND names
    // -h

    DAMAGE(
            Material.DIAMOND_SWORD,
            "&4&l💢&4",
            "Damage",
            "Experts in dealing as much damage as possible."
    ),

    MELEE(
            Material.IRON_SWORD,
            "&c⚔&c",
            "Melee",
            "Experts in close-range battle."
    ),

    RANGE(
            Material.CROSSBOW,
            "&b&l🎯&b",
            "Range",
            "Rangers are dead-eye shooters that can hold distance to strike."
    ),

    TALENT_DAMAGE(
            Material.BLAZE_POWDER,
            "&6\uD83D\uDD25",
            "Talent DPS",
            "Deals most of its damage using talents."
    ),

    POWERFUL_ULTIMATE(
            Material.STRUCTURE_VOID,
            "&b\uD83D\uDCA5",
            "Powerful Ultimate",
            "Possesses a powerful ultimate talent."
    ),

    MOBILITY(
            Material.RABBIT_FOOT,
            "&d👣",
            "Mobility",
            "Fast and mobile, they zip around the battlefield."
    ),

    STRATEGY(
            Material.LIGHT,
            "&e💡",
            "Strategy",
            "Strategists rely on their talents, rather than strength to win."
    ),

    SUPPORT(
            Material.LILY_PAD,
            "&2🍀",
            "Support",
            "Provides buffs."
    ),

    HEALER(
            Material.APPLE,
            "&a❤",
            "Healer",
            "Provides healing effects."
    ),

    HEXBANE(
            Material.NETHERITE_SCRAP,
            Color.HEXBANE + "🕷",
            "Hexbane",
            "Masters of debuffs and weakening enemies."
    ),

    SELF_SUSTAIN(
            Material.POTION,
            Color.MINT_GREEN + "\uD83C\uDF3F",
            "Self Sustain",
            "Able to regenerate oneself health."
    ),

    SELF_BUFF(
            Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
            "&c&l\uD83C\uDF00&c",
            "Enhance",
            "Enhances oneself for the battle."
    ),

    DEFENSE(
            Material.SHIELD,
            Color.GREEN + "🛡",
            "Defense",
            "Provides shields and defenses."
    ),

    NOT_SET; // keep last this is needed for sort

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

    @Nonnull
    public static Set<Archetype> packFromHero(@Nonnull Hero hero) {
        // todo
        return Set.of();
    }
}
