package me.hapyl.fight.game.cosmetic;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class ShopItem {

    @Deprecated
    public static final long NOT_PURCHASABLE = -1L;

    private final String name;
    private final String description;

    private String extra;
    private Rarity rarity;
    private Material icon;

    public ShopItem(String name, String description) {
        this.name = name;
        this.description = description;
        this.extra = null;
        this.rarity = Rarity.UNSET;
        this.icon = Material.BARRIER;
    }

    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = ItemBuilder.of(icon, name);

        builder.addLore(rarity.toString());
        builder.addLore();
        builder.addSmartLore(description);

        if (extra != null) {
            builder.addLore();
            builder.addSmartLore(extra);
        }

        if (isPurchaseable()) {
            builder.addLore();
            // TODO (hapyl): 002, Jul 2:
            //builder.addLore("&eCost: &a%s", instantBuy);
        }

        return builder.addLore();
    }

    @Nullable
    public String getExtra() {
        return extra;
    }

    public ShopItem setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    @Deprecated(forRemoval = true)
    public boolean isPurchaseable() {
        return false;
    }

    public Material getIcon() {
        return icon;
    }

    public ShopItem setIcon(Material icon) {
        this.icon = icon;
        return this;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public ShopItem setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
