package me.hapyl.fight.game.weapons;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Weapon implements Cloneable, DisplayFieldProvider {

    // FIXME: 023, Mar 23, 2023 -> Still using old formatter

    private ItemStack item;

    private final List<Enchant> enchants;
    private final Material material;
    private String name;
    private String description;
    private String lore;
    private double damage;
    private double attackSpeed;

    private String id;

    public Weapon(@Nonnull Material material) {
        this(material, "unnamed weapon", "", 1);
    }

    public Weapon(@Nonnull Material material, @Nonnull String name, @Nonnull String about, double damage) {
        this.material = material;
        this.name = name;
        this.description = about;
        this.attackSpeed = 0.0d;
        this.damage = damage;
        this.enchants = new ArrayList<>();
    }

    public Weapon setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public Weapon addEnchant(Enchantment enchantment, int level) {
        this.enchants.add(new Enchant(enchantment, level));
        return this;
    }

    public Weapon setName(String name) {
        this.name = name;
        return this;
    }

    public Weapon setDescription(String info, Object... replacements) {
        return setDescription(info.formatted(replacements));
    }

    public Weapon setDescription(String lore) {
        this.description = lore;
        return this;
    }

    public Weapon setDamage(double damage) {
        if (damage < 0) {
            damage = 1.0d;
        }

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

    public String getDescription() {
        return description;
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
     * ID is required to use functions.
     */
    public Weapon setId(String id) {
        this.id = id.toUpperCase(Locale.ROOT);
        return this;
    }

    private void createItem() {
        final ItemBuilder builder = this.id == null ? new ItemBuilder(this.material) : new ItemBuilder(
                this.material,
                this.id
        );

        builder.setName(ChatColor.GREEN + notNullStr(this.name, "Standard Weapon"));
        builder.addLore("&8Weapon");

        if (this.description != null) {
            builder.addLore().addSmartLore(description, 35);
        }

        if (this.lore != null && false/*don't add lor for now*/) {
            builder.addLore().addSmartLore(lore, "&8&o");
        }

        if (this instanceof RangeWeapon rangeWeapon) {
            if (rangeWeapon.getCooldown() > 0) {
                builder.addLore();
                builder.addLore("&aReload Time: &l%ss", BukkitUtils.roundTick(rangeWeapon.getCooldown()));
            }
        }

        // add id
        if (id != null) {
            builder.addClickEvent(player -> {
                final Response response = Utils.playerCanUseAbility(player);
                if (response.isError()) {
                    response.sendError(player);
                    return;
                }
                onRightClick(player, player.getInventory().getItemInMainHand());
            }, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR);

            builder.addClickEvent(player -> {
                final Response response = Utils.playerCanUseAbility(player);
                if (response.isError()) {
                    response.sendError(player);
                    return;
                }
                onLeftClick(player, player.getInventory().getItemInMainHand());
            }, Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR);
        }

        if (!enchants.isEmpty()) {
            enchants.forEach(enchant -> builder.addEnchant(enchant.getEnchantment(), enchant.getLevel()));
        }

        if (!isRanged()) {
            builder.addAttribute(
                    Attribute.GENERIC_ATTACK_DAMAGE,
                    damage - 1.0d, // has to be -1 here
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
        }

        if (attackSpeed != 0) {
            builder.addAttribute(
                    Attribute.GENERIC_ATTACK_SPEED,
                    attackSpeed,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
        }

        builder.setUnbreakable(true);

        if (material == Material.BOW || material == Material.CROSSBOW) {
            builder.addEnchant(Enchantment.ARROW_INFINITE, 1);
        }

        if (material == Material.TRIDENT) {
            builder.addEnchant(Enchantment.LOYALTY, 3);
        }

        // don't cancel clicks for these items
        switch (material) {
            case BOW, CROSSBOW, TRIDENT, FISHING_ROD, SHIELD -> builder.setCancelClicks(false);
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
            return new Weapon(this.material).setName(this.name).setDescription(this.description).setDamage(this.damage)
                    .setId(this.id);
        } catch (Exception ignored) {
        }
        return new Weapon(Material.BEDROCK);
    }

    @Nonnull
    public Material getType() {
        return item == null ? Material.AIR : item.getType();
    }

    public Weapon setWeaponLore(String lore) {
        this.lore = lore;
        return this;
    }

    public boolean isRanged() {
        return (material == Material.BOW || material == Material.CROSSBOW || material == Material.TRIDENT) || this instanceof RangeWeapon;
    }

    public void giveWeapon(Player player) {
        player.getInventory().setItem(0, getItem());
    }
}
