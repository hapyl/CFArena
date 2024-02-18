package me.hapyl.fight.game.heroes.equipment;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.story.lore.Lore;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents armor equipment that can be equipped to entities.
 * Weapons can only be applied to entities, not players.
 */
public class Equipment implements Described, Lore {

    private final ItemStack[] items;
    private String name;
    private String description;
    private String flavorText;

    public Equipment() {
        this.items = new ItemStack[6];
        this.name = "";
        this.description = "";
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nonnull
    @Override
    public String getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    @Super
    public Equipment setItem(@Nonnull Slot slot, @Nullable ItemStack item) {
        items[slot.getId()] = item;
        return this;
    }

    public Equipment setHandItem(@Nonnull Material material) {
        return setItem(Slot.HAND, new ItemBuilder(material).cleanToItemSack());
    }

    public Equipment setOffHandItem(@Nonnull Material material) {
        return setItem(Slot.OFFHAND, new ItemBuilder(material).asIcon());
    }

    public Equipment setChestPlate(@Nonnull Material material, @Nonnull TrimPattern pattern, @Nonnull TrimMaterial trimMaterial) {
        return setItem(Slot.CHESTPLATE, new ItemBuilder(material).setArmorTrim(pattern, trimMaterial).cleanToItemSack());
    }

    public Equipment setLeggings(@Nonnull Material material, @Nonnull TrimPattern pattern, @Nonnull TrimMaterial trimMaterial) {
        return setItem(Slot.LEGGINGS, new ItemBuilder(material).setArmorTrim(pattern, trimMaterial).cleanToItemSack());
    }

    public Equipment setBoots(@Nonnull Material material, @Nonnull TrimPattern pattern, @Nonnull TrimMaterial trimMaterial) {
        return setItem(Slot.BOOTS, new ItemBuilder(material).setArmorTrim(pattern, trimMaterial).cleanToItemSack());
    }

    public void setTexture(@Nonnull String texture64) {
        setHelmet(ItemBuilder.playerHeadUrl(texture64).cleanToItemSack());
    }

    public Equipment setHelmet(int red, int green, int blue) {
        return setHelmet(ItemBuilder.leatherHat(Color.fromRGB(red, green, blue)).cleanToItemSack());
    }

    public Equipment setChestPlate(int red, int green, int blue) {
        return setChestPlate(ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue)).cleanToItemSack());
    }

    public Equipment setLeggings(int red, int green, int blue) {
        return setLeggings(ItemBuilder.leatherPants(Color.fromRGB(red, green, blue)).cleanToItemSack());
    }

    public Equipment setBoots(int red, int green, int blue) {
        return setBoots(ItemBuilder.leatherBoots(Color.fromRGB(red, green, blue)).cleanToItemSack());
    }

    public Equipment setChestPlate(int red, int green, int blue, @Nonnull TrimPattern pattern, @Nonnull TrimMaterial material) {
        return setChestPlate(ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue))
                .setArmorTrim(pattern, material)
                .cleanToItemSack());
    }

    public Equipment setLeggings(int red, int green, int blue, @Nonnull TrimPattern pattern, @Nonnull TrimMaterial material) {
        return setLeggings(ItemBuilder.leatherPants(Color.fromRGB(red, green, blue)).setArmorTrim(pattern, material).cleanToItemSack());
    }

    public Equipment setBoots(int red, int green, int blue, @Nonnull TrimPattern pattern, @Nonnull TrimMaterial material) {
        return setBoots(ItemBuilder.leatherBoots(Color.fromRGB(red, green, blue)).setArmorTrim(pattern, material).cleanToItemSack());
    }

    public Equipment setHelmet(@Nullable ItemStack stack) {
        return setItem(Slot.HELMET, stack);
    }

    @Deprecated
    public Equipment setHelmet(@Nonnull Material material) {
        return setItem(Slot.HELMET, new ItemBuilder(material).cleanToItemSack());
    }

    public Equipment setHelmet(@Nonnull Color color) {
        return setHelmet(color.getRed(), color.getGreen(), color.getBlue());
    }

    public Equipment setChestPlate(@Nullable ItemStack stack) {
        return setItem(Slot.CHESTPLATE, stack);
    }

    public Equipment setChestPlate(@Nonnull Material material) {
        return setItem(Slot.CHESTPLATE, new ItemBuilder(material).cleanToItemSack());
    }

    public Equipment setChestPlate(@Nonnull Color color) {
        return setChestPlate(color.getRed(), color.getGreen(), color.getBlue());
    }

    public Equipment setLeggings(@Nullable ItemStack stack) {
        return setItem(Slot.LEGGINGS, stack);
    }

    public Equipment setLeggings(@Nonnull Material material) {
        return setLeggings(new ItemBuilder(material).cleanToItemSack());
    }

    public Equipment setLeggings(@Nonnull Color color) {
        return setLeggings(color.getRed(), color.getGreen(), color.getBlue());
    }

    public Equipment setBoots(@Nullable ItemStack stack) {
        return setItem(Slot.BOOTS, stack);
    }

    public Equipment setBoots(@Nonnull Material material) {
        return setBoots(new ItemBuilder(material).cleanToItemSack());
    }

    public Equipment setBoots(@Nonnull Color color) {
        return setBoots(color.getRed(), color.getGreen(), color.getBlue());
    }

    public final void equip(@Nonnull LivingEntity entity) {
        equipArmor(entity);

        if (entity instanceof Player player) {
            player.updateInventory();
        }
        else {
            equipWeapons(entity);
        }
    }

    public final void equipWeapons(@Nonnull LivingEntity entity) {
        if (entity instanceof Player) {
            throw new IllegalArgumentException("cannot equip weapons on a player");
        }

        final EntityEquipment equipment = getEntityEquipment(entity);

        equipment.setItemInMainHand(getItem(Slot.HAND));
        equipment.setItemInOffHand(getItem(Slot.OFFHAND));
    }

    public final void equipArmor(@Nonnull LivingEntity entity) {
        equipArmor(entity, false);
    }

    public final void equipArmor(@Nonnull LivingEntity entity, boolean silent) {
        final EntityEquipment equipment = getEntityEquipment(entity);

        equipment.setHelmet(getItem(Slot.HELMET), silent);
        equipment.setChestplate(getItem(Slot.CHESTPLATE), silent);
        equipment.setLeggings(getItem(Slot.LEGGINGS), silent);
        equipment.setBoots(getItem(Slot.BOOTS), silent);
    }

    @Nonnull
    public final ItemStack getItem(Slot slot) {
        final ItemStack item = items[slot.getId()];

        return item == null ? ItemStacks.AIR : item;
    }

    public void equip(GamePlayer gamePlayer) {
        equip(gamePlayer.getPlayer());
    }

    public void setFromEquipment(@Nonnull Equipment equipment) {
        for (int i = 0; i < this.items.length; i++) {
            this.items[i] = equipment.items[i];
        }
    }

    @Nonnull
    private EntityEquipment getEntityEquipment(LivingEntity entity) {
        final EntityEquipment equipment = entity.getEquipment();

        if (equipment == null) {
            throw new IllegalArgumentException(entity.getName() + " doesn't have equipment!");
        }

        return equipment;
    }

}
