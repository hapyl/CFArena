package kz.hapyl.fight.game.weapons;

import kz.hapyl.fight.game.weapons.storage.ArcherBow;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Weapons {

	DEFAULT(new Weapon(Material.WOODEN_SWORD).setName("Standard Weapon").setDamage(10)),
	ARCHER_BOW(new ArcherBow().setDamage(5.0)),

	;

	private final Weapon weapon;

	Weapons(Weapon weapon) {
		this.weapon = weapon;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public ItemStack getItem() {
		return this.getWeapon().getItem();
	}

}
