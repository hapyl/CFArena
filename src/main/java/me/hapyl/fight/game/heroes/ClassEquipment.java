package me.hapyl.fight.game.heroes;

import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Locale;

public class ClassEquipment {

	private final static ItemStack NULL_ITEM = ItemStacks.AIR;

	private final ItemStack[] armor;

	public ClassEquipment() {
		this.armor = new ItemStack[4];
	}

	public void setArmor(ItemStack[] stack) {
		if (stack.length == 4) {
			System.arraycopy(stack, 0, this.armor, 0, this.armor.length);
		}
	}

	public ClassEquipment setHelmet(ItemStack stack) {
		this.armor[0] = stack;
		return this;
	}

    @Deprecated
    public ClassEquipment setHelmet(Material material) {
        this.armor[0] = new ItemBuilder(material).cleanToItemSack();
        return this;
    }

	public ClassEquipment setChestplate(Material material) {
		this.armor[1] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setLeggings(Material material) {
		this.armor[2] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setBoots(Material material) {
		this.armor[3] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setArmorPiece(Material material) {
		final String lowerName = material.name().toLowerCase(Locale.ROOT);
		switch (lowerName) {
			case "helmet" -> {
				return this.setHelmet(material);
			}
			case "chestplate" -> {
				return this.setChestplate(material);
			}
			case "leggings" -> {
				return this.setLeggings(material);
			}
			case "boots" -> {
				return this.setBoots(material);
			}
			default -> throw new IllegalArgumentException(String.format("couldn't figure out where to put %s!", material.name()));
		}

	}

	public ClassEquipment setHelmet(String headTexture) {
		return this.setHelmet(ItemBuilder.playerHead(headTexture).cleanToItemSack());
	}

	public ClassEquipment setTexture(String texture64) {
		return this.setHelmet(ItemBuilder.playerHeadUrl(texture64).cleanToItemSack());
	}

	public ClassEquipment setHelmet(int red, int green, int blue) {
		return this.setHelmet(ItemBuilder.leatherHat(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment setChestplate(ItemStack stack) {
		this.armor[1] = stack;
		return this;
	}

	public ClassEquipment setChestplate(int red, int green, int blue) {
		return this.setChestplate(ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment setLeggings(ItemStack stack) {
		this.armor[2] = stack;
		return this;
	}

	public ClassEquipment setLeggings(int red, int green, int blue) {
		return this.setLeggings(ItemBuilder.leatherPants(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment setBoots(ItemStack stack) {
		this.armor[3] = stack;
		return this;
	}

	public ClassEquipment setBoots(int red, int green, int blue) {
		return this.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	@Nonnull
	public ItemStack getHelmet() {
		return this.itemOrNull(this.armor[0]);
	}

	@Nonnull
	public ItemStack getChestplate() {
		return this.itemOrNull(this.armor[1]);
	}

	@Nonnull
	public ItemStack getLeggings() {
		return this.itemOrNull(this.armor[2]);
	}

	@Nonnull
	public ItemStack getBoots() {
		return this.itemOrNull(this.armor[3]);
	}

	public void equip(Player player) {
		this.equipArmor(player);
		player.updateInventory();
	}

	public void equipArmor(Player player) {
		final EntityEquipment equipment = player.getEquipment();
		if (equipment != null) {
			equipment.setHelmet(this.armorOrNull(0));
			equipment.setChestplate(this.armorOrNull(1));
			equipment.setLeggings(this.armorOrNull(2));
			equipment.setBoots(this.armorOrNull(3));
		}
	}

	public void unequip(Player player) {
		player.getInventory().clear();
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	private ItemStack armorOrNull(int index) {
		return this.itemOrNull(this.armor[index]);
	}

	private ItemStack itemOrNull(ItemStack s) {
		return s == null ? NULL_ITEM : s;
	}

	public void setHelmet(Color color) {
		this.setHelmet(color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setChestplate(Color color) {
		this.setChestplate(color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setLeggings(Color color) {
		this.setLeggings(color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setBoots(Color color) {
		this.setBoots(color.getRed(), color.getGreen(), color.getBlue());
	}

}
