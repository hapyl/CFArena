package me.hapyl.fight.game.cosmetic;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CollectionItem {

    private final String name;
    private final String description;

    private Rarity rarity;
    private Material icon;
    private boolean exclusive;

    public CollectionItem(String name, String description) {
        this.name = name;
        this.description = description;
        this.rarity = Rarity.UNSET;
        this.icon = Material.BARRIER;
    }

    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = ItemBuilder.of(icon, name);

        builder.addLore(rarity.toString());
        builder.addLore();
        builder.addSmartLore(description);

        addExtraLore(builder, player);

        if (isExclusive()) {
            builder.addLore();
            // TODO (hapyl): 002, Jul 2:
            //builder.addLore("&eCost: &a%s", instantBuy);
        }

        return builder.addLore();
    }

    public void addExtraLore(@Nonnull ItemBuilder builder, @Nonnull Player player) {
    }

    /**
     * Sets if this cosmetics is exclusive.
     * Exclusive cosmetics cannot be dropped from crates or bought.
     *
     * @param exclusive - Is explosive.
     */
    public CollectionItem setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    /**
     * Returns true if this cosmetic is exclusive.
     * Exclusive cosmetics cannot be dropped from crates or bought.
     *
     * @return true if this cosmetic is exclusive, false otherwise.
     */
    public boolean isExclusive() {
        return exclusive;
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
