package kz.hapyl.fight.game.weapons;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Weapon {

	private ItemStack item;

	private final Material material;
	private String name;
	private String lore;
	private double damage;

	public Weapon(Material material) {
		this.material = material;
	}

	public Weapon setName(String name) {
		this.name = name;
		return this;
	}

	public void onLeftClick(Player player) {

	}

	public void onRightClick(Player player) {

	}

	public Weapon setDamage(double damage) {
		this.damage = damage;
		return this;
	}

	public double getDamage() {
		return damage;
	}

	@Nullable
	public ItemStack getItem() {
		if (this.item == null) {
			this.createItem();
		}
		return this.item;
	}

	private void createItem() {
		final ItemBuilder builder = new ItemBuilder(this.material);
		builder.setName(ChatColor.GREEN + notNullStr(this.name, "Standard Weapon"));

		if (this.lore != null) {
			builder.addSmartLore(lore);
		}

		builder.setPureDamage(this.damage);
		builder.setUnbreakable(true);

		if (this.material == Material.BOW || this.material == Material.CROSSBOW) {
			builder.addEnchant(Enchantment.ARROW_INFINITE, 1);
		}

		builder.hideFlags();
		this.item = builder.build();
	}

	private String notNullStr(String str, String def) {
		return str == null ? def : str;
	}


}
