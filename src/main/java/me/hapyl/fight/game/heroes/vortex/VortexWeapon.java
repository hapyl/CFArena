package me.hapyl.fight.game.heroes.vortex;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;

public class VortexWeapon extends Weapon {

    public VortexWeapon() {
        super(Material.STONE_SWORD, Key.ofString("sots"));

        setName("Sword of Thousands Stars");
        setDescription("A sword with an astral link to the stars.");
        setDamage(6.5d);
    }

}
