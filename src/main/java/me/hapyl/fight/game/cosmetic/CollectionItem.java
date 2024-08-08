package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CollectionItem {

    protected final String name;
    protected String description;

    protected Rarity rarity;
    protected Material icon;
    protected String texture;

    private boolean exclusive;

    public CollectionItem(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
        this.rarity = Rarity.UNSET;
        this.icon = Material.BARRIER;
    }

    public boolean canObtain(@Nonnull OfflinePlayer player) {
        return true;
    }

    @Nonnull
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = ItemBuilder.of(icon, name);

        builder.addLore(rarity.toString());
        builder.addLore();
        builder.addTextBlockLore(description);

        if (texture != null) {
            builder.setHeadTextureUrl(texture);
        }

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
     * Returns true if this cosmetic is exclusive.
     * Exclusive cosmetics cannot be dropped from crates or bought.
     *
     * @return true if this cosmetic is exclusive, false otherwise.
     */
    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * Sets if this cosmetics is exclusive.
     * Exclusive cosmetics cannot be dropped from crates or bought.
     *
     * @param exclusive - Is exclusive.
     */
    public CollectionItem setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
        return this;
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

    public void setDescription(String description, Object... format) {
        this.description = description.formatted(format);
    }

    public CollectionItem setTexture(String texture) {
        this.texture = texture;
        return this;
    }
}
