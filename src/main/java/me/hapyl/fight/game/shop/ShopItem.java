package me.hapyl.fight.game.shop;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class ShopItem {

    public static final long NOT_PURCHASABLE = -1L;

    private final String name;
    private final String description;
    private final long cost;

    private String extra;
    private Rarity rarity;
    private Material icon;

    public ShopItem(String name, String description, long cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.extra = null;
        this.rarity = Rarity.UNSET;
        this.icon = Material.BARRIER;
    }

    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = ItemBuilder.of(icon, name);

        builder.addLore();
        builder.addSmartLore(description);

        if (extra != null) {
            builder.addLore();
            builder.addSmartLore(extra);
        }

        if (isPurchaseable()) {
            builder.addLore();
            builder.addLore("&eCost: &a%s", cost);
        }

        return builder.addLore();
    }

    public ShopItem setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    @Nullable
    public String getExtra() {
        return extra;
    }

    public boolean isPurchaseable() {
        return cost != NOT_PURCHASABLE;
    }

    public ShopItem setIcon(Material icon) {
        this.icon = icon;
        return this;
    }

    public Material getIcon() {
        return icon;
    }

    public ShopItem setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getCost() {
        return cost;
    }

}
