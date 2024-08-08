package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.util.SmallCaps;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public enum Type {

    KILL(Material.IRON_SWORD, "Kill Cosmetic", "Executes whenever you eliminate an opponent."),
    DEATH(Material.SKELETON_SKULL, "Death Cosmetic", "Executes whenever you die."),
    CONTRAIL(Material.LEAD, "Contrail", "Trails behind you."),
    WIN(Material.DIAMOND, "Win Cosmetic", "Displays when you win a game."),
    PREFIX(Material.PAPER, "Status", "Prefixes the player's name."),
    GADGET(Material.LEVER, "Gadget", "Have fun in the lobby!"),
    //HAT(Material.PLAYER_HEAD, "Displays on top of your head."),
    //GRADIENT(Material.GRAY_DYE, "Changes the kill and death message gradient."),
    ;

    private final Material material;
    private final String name;
    private final String description;

    Type(Material material, String name, String description) {
        this.material = material;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nonnull
    public String toSmallCaps() {
        return SmallCaps.format(name);
    }

    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }
}
