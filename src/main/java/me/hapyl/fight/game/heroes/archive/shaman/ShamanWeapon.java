package me.hapyl.fight.game.heroes.archive.shaman;

import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class ShamanWeapon extends Weapon {
    public ShamanWeapon() {
        super(Material.BAMBOO);

        setName("Bamboo Stick");
        setDescription("""
                An ordinary stick found in the jungle.
                """);
        setId("shaman_weapon");

        setDamage(5.0d);
        addEnchant(Enchantment.KNOCKBACK, 1);
    }
}
