package me.hapyl.fight.game.cosmetic;

import org.bukkit.Material;

public enum Type {

    KILL(Material.IRON_SWORD, "Kill", "Executes whenever you eliminate an opponent."),
    DEATH(Material.SKELETON_SKULL, "Death", "Executes whenever you die."),
    CONTRAIL(Material.LEAD, "Contrail", "Trails behind you."),
    WIN(Material.DIAMOND, "Win", "Displays when you win a game."),
    PREFIX(Material.PAPER, "Status", "Prefixes the player's name."),
    //HAT(Material.PLAYER_HEAD, "Displays on top of your head."),
    //GRADIENT(Material.GRAY_DYE, "Changes the kill and death message gradient."),
    ;

    private final Material material;
    private final String name;
    private final String description;

    Type(Material material, String name, String description) {
        this.material = material;
        this.name = name + " Cosmetic";
        this.description = description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }
}
