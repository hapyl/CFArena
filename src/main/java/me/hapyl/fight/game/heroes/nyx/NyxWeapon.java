package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;

public class NyxWeapon extends Weapon {
    public NyxWeapon() {
        super(Material.NETHERITE_SWORD, Key.ofString("entropys_edge"));

        setName("Entropy's Edge");
        setDescription("""
                It's all chaos...
                """);

        setDamage(4.0d);
    }
}
