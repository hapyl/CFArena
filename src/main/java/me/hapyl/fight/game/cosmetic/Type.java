package me.hapyl.fight.game.cosmetic;

import org.bukkit.Material;

public enum Type {

    KILL(Material.IRON_SWORD, "Executes whenever you eliminate an opponent."),
    DEATH(Material.SKELETON_SKULL, "Executes whenever you die."),
    CONTRAIL(Material.LEAD, "Trails behind you."),
    WIN(Material.DIAMOND, "Displays when you win a game."),
    PREFIX(Material.PAPER, "Prefixes the player's name."),
    //HAT(Material.PLAYER_HEAD, "Displays on top of your head."),
    //GRADIENT(Material.GRAY_DYE, "Changes the kill and death message gradient."),
    ;

    private final Material material;
    private final String description;

    Type(Material material, String description) {
        this.material = material;
        this.description = description;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }
}
