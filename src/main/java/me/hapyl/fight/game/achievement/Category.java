package me.hapyl.fight.game.achievement;

import org.bukkit.Material;

/**
 * Represents an achievement category.
 */
public enum Category {

    GAMEPLAY(Material.IRON_SWORD, "Gameplay", "Main gameplay achievements."),
    HERO_PLAYER(Material.PLAYER_HEAD, "Hero Player", "Play heroes specific amount of times."),
    HERO_WINNER(Material.DIAMOND, "Hero Winner", "Win as a hero specific amount of times."),

    ;

    private final Material material;
    private final String name;
    private final String description;

    Category(Material material, String name, String description) {
        this.material = material;
        this.name = name;
        this.description = description;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
