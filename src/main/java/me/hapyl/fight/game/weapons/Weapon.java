package me.hapyl.fight.game.weapons;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.NonNullItemCreator;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
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
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Weapon extends NonNullItemCreator implements Cloneable, DisplayFieldProvider {

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
        this.enchants = Lists.newArrayList();
    }

    /**
     * Sets the weapon attack speed.
     * Use <code>/setattackspeed (value)</code> in game to test this, the value is quite arbitrary.
     *
     * @param attackSpeed - Attack speed.
     */
    public Weapon setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public Weapon addEnchant(Enchantment enchantment, int level) {
        this.enchants.add(new Enchant(enchantment, level));
        return this;
    }

    public Weapon setDescription(String info, Object... replacements) {
        return setDescription(info.formatted(replacements));
    }

    public String getName() {
        return name;
    }

    public Weapon setName(String name) {
        this.name = name;
        return this;
    }

    @Nullable
    public String getId() {
        return id;
    }

    /**
     * ID is required to use functions.
     */
    public Weapon setId(String id) {
        this.id = id.toUpperCase(Locale.ROOT);
        return this;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }

    public Weapon setDescription(String lore) {
        this.description = lore;
        return this;
    }

    public double getDamage() {
        return damage;
    }

    public Weapon setDamage(double damage) {
        if (damage < 0) {
            damage = 1.0d;
        }

        this.damage = damage;
        return this;
    }

    public void onLeftClick(Player player, ItemStack item) {
    }

    public void onRightClick(Player player, ItemStack item) {
    }

    @Override
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
        return this instanceof RangeWeapon || (material == Material.BOW || material == Material.CROSSBOW || material == Material.TRIDENT);
    }

    public void give(Player player) {
        player.getInventory().setItem(0, getItem());
    }

    public void createItem() {
        final ItemBuilder builder = this.id == null ? new ItemBuilder(this.material) : new ItemBuilder(
                this.material,
                this.id
        );

        builder.setName(ChatColor.GREEN + notNullStr(this.name, "Standard Weapon"));
        builder.addLore(this instanceof RangeWeapon ? "&8Ranged Weapon" : "&8Weapon");

        if (this.description != null) {
            // I SWEAR TO GOD IF I GET ONE MORE
            // MISSING, FUCKING, FORMATTER ERROR
            description = description.replace("%", "%%");
            builder.addLore().addTextBlockLore(description);
        }

        if (this.lore != null && false/*don't add lore for now*/) {
            builder.addLore().addSmartLore(lore, "&8&o");
        }

        if (this instanceof RangeWeapon rangeWeapon) {
            final int reloadTime = rangeWeapon.getWeaponCooldown();
            final double maxDistance = rangeWeapon.getMaxDistance();
            final double weaponDamage = rangeWeapon.getDamage();

            builder.addLore();
            builder.addLore("&aLeft-Click to manually reload!");
            builder.addLore();
            builder.addLore("&e&lAttributes:");

            addDynamicLore(builder, " Fire Rate: &f&l%s", reloadTime, t -> Tick.round(t.intValue()) + "s");
            addDynamicLore(builder, " Max Distance: &f&l%s", maxDistance, t -> t + "s");
            addDynamicLore(builder, " Damage: &f&l%s", weaponDamage, Object::toString);

            builder.addLore(" Max Ammo: &f&l%s", rangeWeapon.getMaxAmmo());
            builder.addLore(" Reload Time: &f&l%s", Tick.round(rangeWeapon.getReloadTime()) + "s");
        }

        // Add click events
        if (id != null) {
            builder.addClickEvent(player -> {
                final Response response = Talent.preconditionTalent(player);

                if (response.isError()) {
                    response.sendError(player);
                    return;
                }

                onRightClick(player, player.getInventory().getItemInMainHand());
            }, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR);

            builder.addClickEvent(player -> {
                final Response response = Talent.preconditionTalent(player);

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

        // I really don't think this is needed with
        // the new system.
        if (!isRanged()) {
            builder.addAttribute(
                    Attribute.GENERIC_ATTACK_DAMAGE,
                    damage - 1.0d, // has to be -1 here
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
        }

        if (attackSpeed != 0.0d) {
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

    private void addDynamicLore(@Nonnull ItemBuilder builder, @Nonnull String string, @Nonnull Number number, Function<Number, String> function) {
        final int value = number.intValue();

        // Since damage cannot be negative, have to handle both -1 and 1.
        // But that also means that if there is a real value of 1, it will be
        // considered as 'Dynamic' in lore.
        // Realistically, though, neither of the values should be 1.
        // (Cooldown is in ticks, hence 20 is 1 b)
        builder.addLore(string.formatted((value == -1 || value == 1) ? "Dynamic" : function.apply(number)));
    }

    private String notNullStr(String str, String def) {
        return str == null ? def : str;
    }
}
