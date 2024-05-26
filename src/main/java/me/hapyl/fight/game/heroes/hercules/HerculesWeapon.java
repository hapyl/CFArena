package me.hapyl.fight.game.heroes.hercules;

import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.DummyAbility;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class HerculesWeapon extends Weapon {
    public HerculesWeapon() {
        super(Material.TRIDENT);

        setName("Gorynych");
        setDescription("A loyal trident which will return to you no matter what!");
        setDamage(10);
        addEnchant(Enchantment.LOYALTY, 3);

        setAbility(AbilityType.RIGHT_CLICK, DummyAbility.of("Thwack", "Throw the trident to deal range damage."));
    }
}
