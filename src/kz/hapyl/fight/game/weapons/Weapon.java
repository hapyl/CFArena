package kz.hapyl.fight.game.weapons;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Weapon implements Cloneable {

	private ItemStack item;

	private final List<Enchant> enchants;
	private final Material material;
	private String name;
	private String lore;
	private double damage;

	private String id;

	public Weapon(Material material) {
		this.material = material;
		this.enchants = new ArrayList<>();
	}

	public Weapon addEnchant(Enchantment enchantment, int level) {
		this.enchants.add(new Enchant(enchantment, level));
		return this;
	}

	public Weapon setName(String name) {
		this.name = name;
		return this;
	}

	public Weapon setLore(String lore) {
		this.lore = lore;
		return this;
	}

	public Weapon setDamage(double damage) {
		this.damage = damage;
		return this;
	}

	public String getName() {
		return name;
	}

	@Nullable
	public String getId() {
		return id;
	}

	public Material getMaterial() {
		return material;
	}

	public String getLore() {
		return lore;
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

	public void onLeftClick(Player player, ItemStack item) {
	}

	public void onRightClick(Player player, ItemStack item) {
	}

	/**
	 * Id is required to use functions.
	 */
	public Weapon setId(String id) {
		this.id = id.toUpperCase(Locale.ROOT);
		return this;
	}

	private void createItem() {
		final ItemBuilder builder = this.id == null ? new ItemBuilder(this.material) : new ItemBuilder(this.material, this.id);
		builder.setName(ChatColor.GREEN + notNullStr(this.name, "Standard Weapon"));

		builder.addLore("&8Weapon");

		if (this.lore != null) {
			builder.addLore().addSmartLore(lore);
		}

		if (this instanceof RangeWeapon rangeWeapon) {
			if (rangeWeapon.getCooldown() > 0) {
				builder.addLore("&9Reload Time: &l%ss", BukkitUtils.roundTick(rangeWeapon.getCooldown()));
			}
		}

		// add id
		if (id != null) {
			builder.addClickEvent(player -> {
				final Response response = Utils.playerCanUseAbility(player);
				if (response.isError()) {
					Chat.sendMessage(player, "&cUnable to use! " + response.getReason());
					return;
				}
				onRightClick(player, player.getInventory().getItemInMainHand());
			}, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR);
			builder.addClickEvent(player -> {
				final Response response = Utils.playerCanUseAbility(player);
				if (response.isError()) {
					Chat.sendMessage(player, "&cUnable to use! " + response.getReason());
					return;
				}
				onLeftClick(player, player.getInventory().getItemInMainHand());
			}, Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR);
		}

		if (!enchants.isEmpty()) {
			enchants.forEach(enchant -> {
				builder.addEnchant(enchant.getEnchantment(), enchant.getLevel());
			});
		}

		builder.addAttribute(
				Attribute.GENERIC_ATTACK_DAMAGE,
				damage - 1.0d, // have to be -1 here
				AttributeModifier.Operation.ADD_NUMBER,
				EquipmentSlot.HAND
		);
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

	public Weapon clone() {
		try {
			super.clone();
			return new Weapon(this.material).setName(this.name).setLore(this.lore).setDamage(this.damage).setId(this.id);
		}
		catch (Exception ignored) {
		}
		return new Weapon(Material.BEDROCK);
	}


}
