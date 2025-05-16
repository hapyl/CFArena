package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.store.Purchasable;
import me.hapyl.fight.util.Formatted;
import me.hapyl.fight.util.strict.StrictValidator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AutoRegisteredListener
public abstract class Cosmetic implements Keyed, Purchasable, Formatted {

    private final Key key;
    private final String name;
    private final Type type;

    protected String description;
    protected Rarity rarity;
    protected Material icon;
    protected String texture;

    protected CosmeticPreview preview;
    private boolean exclusive;

    public Cosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull Type type) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.description = "No description.";
        this.rarity = Rarity.UNSET;
        this.icon = Material.BEDROCK;

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }

        StrictValidator.validateClassName(this.getClass(), ".*Cosmetic$");
    }

    public boolean isNotDisabledNorExclusive() {
        return !(this instanceof Disabled) && !exclusive;
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    @Override
    public long getPrice() {
        return rarity.getPrice();
    }

    @Nonnull
    @Override
    public Currency getCurrency() {
        return rarity.getCurrency();
    }

    public void setTexture(@Nonnull String texture) {
        this.texture = texture;
    }

    @Nonnull
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = new ItemBuilder(icon);

        builder.setName(name);
        builder.addLore(rarity.toString(type.getName()));
        builder.addLore("");
        builder.addTextBlockLore(getDescription());

        if (texture != null) {
            builder.setType(Material.PLAYER_HEAD);
            builder.setHeadTextureUrl(texture);
        }

        return builder;
    }

    public final void onDisplay0(@Nonnull Display display) {
        final Player player = display.getPlayer();

        if (this instanceof Disabled disabled) {
            if (player != null) {
                disabled.errorMessage(player, "cosmetic");
                return;
            }
            
            return;
        }

        onDisplay(display);
    }

    @EventLike
    public void onEquip(@Nonnull Player player) {
    }

    @EventLike
    public void onUnequip(@Nonnull Player player) {
    }

    @Nonnull
    @Override
    public String getFormatted() {
        return ChatColor.GREEN + getName() + " &7(" + type.getName() + ") " + getRarity().getFormatted() + "&7";
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    public boolean hasPreview() {
        return preview != null;
    }

    @Nonnull
    public CosmeticPreview getPreview() {
        return preview;
    }

    public abstract void onDisplay(@Nonnull Display display);

    public boolean canObtain(@Nonnull OfflinePlayer player) {
        return true;
    }

    /**
     * Returns true if this cosmetic is exclusive.
     * Exclusive cosmetics cannot appear in store.
     *
     * @return true if this cosmetic is exclusive, false otherwise.
     */
    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(@Nonnull Material icon) {
        this.icon = icon;
    }

    @Nonnull
    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(@Nonnull Rarity rarity) {
        this.rarity = rarity;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    public boolean isUnlocked(@Nonnull Player player) {
        return getCosmetics(player).isUnlocked(this);
    }

    public void setUnlocked(@Nonnull Player player, boolean unlocked) {
        getCosmetics(player).setUnlocked(this, unlocked);
    }

    public boolean isSelected(@Nonnull Player player) {
        return getCosmetics(player).getSelected(getType()) == this;
    }

    public void select(@Nonnull Player player) {
        getCosmetics(player).setSelected(getType(), this);
    }

    public void deselect(@Nonnull Player player) {
        getCosmetics(player).unsetSelected(getType());
    }

    @Nonnull
    public String getRarityString() {
        return getRarity().toString();
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Cosmetic that = (Cosmetic) object;
        return Objects.equals(this.key, that.key);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }

    private CosmeticEntry getCosmetics(@Nonnull Player player) {
        return CF.getDatabase(player).cosmeticEntry;
    }

    @Nonnull
    public static Cosmetic[] getCosmeticsThatCanAppearInStoreAndNotOwnedBy(@Nullable Player player) {
        final CosmeticRegistry registry = Registries.cosmetics();

        final Cosmetic nullCosmetic = getNullCosmetic();
        final Cosmetic[] defaultStoreOffers = { nullCosmetic, nullCosmetic, nullCosmetic, nullCosmetic };

        if (player == null) {
            return defaultStoreOffers;
        }

        final List<Cosmetic> potentialCosmetics = Lists.newArrayList();

        for (Cosmetic cosmetic : registry.values()) {
            if (cosmetic.isNotDisabledNorExclusive() && cosmetic.isPurchasable()) {
                potentialCosmetics.add(cosmetic);
            }
        }

        potentialCosmetics.removeIf(cosmetic -> cosmetic.isUnlocked(player));

        // TODO: Maybe rarer cosmetics should appear, well, rarer?
        Collections.shuffle(potentialCosmetics);

        for (int i = 0; i < Math.clamp(potentialCosmetics.size(), 0, defaultStoreOffers.length); i++) {
            defaultStoreOffers[i] = potentialCosmetics.get(i);
        }

        return defaultStoreOffers;
    }

    @Nonnull
    public static Cosmetic getNullCosmetic() {
        return Registries.cosmetics().NULL_COSMETIC;
    }
}
