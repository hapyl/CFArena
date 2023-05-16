package me.hapyl.fight.game.achievement;

import org.bukkit.Material;

/**
 * Represents an achievement category.
 */
public enum Category {

    GAMEPLAY(Material.IRON_SWORD, "Gameplay", ""),
    HERO_PLAYER(Material.PLAYER_HEAD, "Hero Player", ""),
    HERO_WINNER(Material.DIAMOND, "Hero Winner", ""),

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
