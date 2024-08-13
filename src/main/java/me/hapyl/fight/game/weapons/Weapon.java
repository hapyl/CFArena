package me.hapyl.fight.game.weapons;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.ItemFunction;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.NonNullItemCreator;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.element.ElementHandler;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.StaticFormat;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Copyable;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class Weapon extends NonNullItemCreator implements Described, DisplayFieldProvider, Copyable, ElementHandler, PlayerElementHandler {

    public static final int DEFAULT_BOW_COOLDOWN = 15;

    private final Map<AbilityType, Ability> abilities;
    private final List<Enchant> enchants;
    private final Material material;
    protected double damage;
    private String name;
    private String description;
    private String lore;
    private String id;

    public Weapon(@Nonnull Material material) {
        this(material, "Unnamed weapon.", "No description provided.", 1);
    }

    public Weapon(@Nonnull Material material, @Nonnull String name, @Nonnull String about, double damage) {
        this.material = material;
        this.name = name;
        this.description = about;
        this.damage = damage;
        this.enchants = Lists.newArrayList();
        this.abilities = Maps.newLinkedHashMap();
    }

    public void removeAbility(@Nonnull AbilityType type) {
        this.abilities.remove(type);
    }

    public void setAbility(@Nonnull AbilityType type, @Nullable Ability ability) {
        if (ability == null) {
            this.abilities.remove(type);
            return;
        }

        if (!ability.isTypeApplicable(type)) {
            throw new IllegalArgumentException("Ability type %s is not applicable to %s!".formatted(
                    type.name(),
                    ability.getClass().getSimpleName()
            ));
        }

        // I guess this is fine?
        // FIXME (hapyl): 020, Nov 20: Might break cooldown for weapons that have multiple abilities
        ability.setCooldownMaterial(material);
        this.abilities.put(type, ability);
    }

    @Nullable
    public Ability getAbility(@Nonnull AbilityType type) {
        return abilities.get(type);
    }

    @Nonnull
    public Optional<Ability> getAbilityOptional(@Nonnull AbilityType type) {
        final Ability ability = getAbility(type);

        return ability == null ? Optional.empty() : Optional.of(ability);
    }

    public Weapon addEnchant(Enchantment enchantment, int level) {
        this.enchants.add(new Enchant(enchantment, level));
        return this;
    }

    public Weapon setDescription(String info, Object... replacements) {
        return setDescription(info.formatted(replacements));
    }

    @Override
    @Nonnull
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
     * ID is required to use abilities.
     */
    public Weapon setId(String id) {
        this.id = id.toUpperCase(Locale.ROOT);
        return this;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    @Override
    @Nonnull
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

    /**
     * @deprecated {@link Weapon#getMaterial}
     */
    @Deprecated
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

    public void give(GamePlayer player) {
        player.setItem(HotbarSlots.WEAPON, getItem());
    }

    public void give(LivingGameEntity entity) {
        final EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(getItem());
    }

    @Nonnull
    public ItemStack createItem() {
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

        // Display abilities
        abilities.forEach((type, ability) -> {
            builder.addLore();
            builder.addLore("&eAbility: " + ability.getName() + Color.BUTTON.bold() + " " + type.toString());

            String description = ability.getDescription();

            description = StaticFormat.COOLDOWN.format(description, ability);
            description = StaticFormat.DURATION.format(description, ability);
            description = description.replace("%", "%%"); // YEP KOK

            builder.addTextBlockLore(description);

            final int duration = ability.getDuration();
            final int cooldown = ability.getCooldown();

            if (duration > 0 || cooldown > 0) {
                builder.addLore();
            }

            builder.addLoreIf("&f•&f &7Cooldown: &f&l" + CFUtils.formatTick(cooldown), cooldown > 0);
            builder.addLoreIf("&f•&f &7Duration: &f&l" + CFUtils.formatTick(duration), duration > 0);

            final Action[] clickTypes = type.getClickTypes();
            if (clickTypes != null) {
                if (id == null) {
                    throw new IllegalArgumentException("Ability for weapon '%s' is set, but the weapon is missing Id!".formatted(getName()));
                }

                final ItemFunction function = builder.addFunction(player -> {
                    final GamePlayer gamePlayer = CF.getPlayer(player);

                    if (gamePlayer == null) {
                        return;
                    }

                    Talent.preconditionAnd(gamePlayer)
                            .ifTrue((pl, rs) -> ability.execute0(pl, pl.getInventory().getItemInMainHand()))
                            .ifFalse((pl, rs) -> rs.sendError(pl));
                });

                for (Action action : clickTypes) {
                    function.accept(action);
                }

                // Don't cancel clicks for these items
                switch (material) {
                    case BOW, CROSSBOW, TRIDENT, FISHING_ROD, SHIELD -> function.setCancelClicks(false);
                }
            }
        });

        appendLore(builder);

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

        // Set attack speed

        // The attack speed is now generalized,
        // meaning all weapons work the same with
        // the same attack speed regardless of vanilla attack speed
        builder.modifyMeta(meta -> {
            meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);

            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(
                    BukkitUtils.createKey(UUID.randomUUID()),
                    0.0d,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.HAND
            ));
        });

        builder.setUnbreakable(true);

        if (material == Material.BOW || material == Material.CROSSBOW) {
            builder.addEnchant(Enchantment.INFINITY, 1);
        }

        if (material == Material.TRIDENT) {
            builder.addEnchant(Enchantment.LOYALTY, 3);
        }

        builder.hideFlags();

        return builder.build();
    }

    @Nonnull
    public Set<Ability> getAbilities() {
        return Sets.newHashSet(abilities.values());
    }

    public boolean hasAbilities() {
        return !abilities.isEmpty();
    }

    public void clearAbilities() {
        abilities.clear();
    }

    /**
     * Create a copy of this weapon.
     * <p>
     * {@link #id} and {@link #abilities} are <b>not</b> copied!
     */
    @Nonnull
    public Weapon createCopy() {
        return new Weapon(material)
                .setName(name)
                .setDescription(description)
                .setDamage(damage);
    }

    protected void addDynamicLore(@Nonnull ItemBuilder builder, @Nonnull String string, @Nonnull Number number, Function<Number, String> function) {
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
