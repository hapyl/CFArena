package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class AlchemistStick extends Weapon {
	public AlchemistStick() {
		super(Material.STICK);
		this.setDamage(5.0);
		this.setName("Stick");
		this.setLore("Turns out that a stick used in brewing can be used in battle.");
		this.addEnchant(Enchantment.KNOCKBACK, 1);
	}
}
