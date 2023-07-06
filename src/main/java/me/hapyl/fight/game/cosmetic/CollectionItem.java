package me.hapyl.fight.game.cosmetic;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectionItem {

    private final String name;
    private final String description;

    private String extra;
    private Rarity rarity;
    private Material icon;
    private boolean exclusive;

    public CollectionItem(String name, String description) {
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

        if (isExclusive()) {
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

    public CollectionItem setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public Material getIcon() {
        return icon;
    }

    public CollectionItem setIcon(Material icon) {
        this.icon = icon;
        return this;
    }

    @Nonnull
    public Rarity getRarity() {
        return rarity == null ? Rarity.UNSET : rarity;
    }

    public CollectionItem setRarity(Rarity rarity) {
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
