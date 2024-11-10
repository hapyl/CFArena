package me.hapyl.fight.game.heroes.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class ShamanWeapon extends Weapon {
    public ShamanWeapon() {
        super(Material.BAMBOO, Key.ofString("shaman_weapon"));

        setName("Bamboo Stick");
        setDescription("""
                An ordinary stick found in the jungle.
                """);

        setDamage(5.0d);
        addEnchant(Enchantment.KNOCKBACK, 1);
    }
}
