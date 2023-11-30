package me.hapyl.fight.game.cosmetic.crate;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.*;
import me.hapyl.fight.util.Described;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Crate extends RandomLoot<Cosmetics> implements Described {

    private final Material material;
    private final String name;
    private String description;

    public Crate(@Nonnull String name) {
        this(Material.TRAPPED_CHEST, name);
    }

    public Crate(@Nonnull Material material, @Nonnull String name) {
        if (!material.isItem()) {
            throw new IllegalArgumentException("material must be an item, %s is not!".formatted(material));
        }

        this.material = material;
        this.name = name;
        this.description = "";
    }

    public Material getMaterial() {
        return material;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public Crate setDescription(@Nonnull String description) {
        this.description = description;
        return this;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    public Cosmetics firstItem() {
        return getContents().firstItem();
    }

    @Nonnull
    public Color firstItemColor() {
        final Cosmetics item = firstItem();

        if (item == null) {
            return Color.GRAY;
        }

        return item.getRarity().getColor();
    }

    /**
     * Adds all items from a given collection to the contents of this crate.
     *
     * @param collection - Collection.
     */
    public Crate with(@Nonnull CosmeticCollection collection) {
        final ItemContents<Cosmetics> contents = getContents();

        for (BelongsToCollection item : collection.getItems()) {
            if (item instanceof Cosmetics cosmetics) {
                contents.addItem(cosmetics);
            }
        }

        updateSchema();
        return this;
    }

    /**
     * Adds all the given items to the contents of this crate.
     *
     * @param cosmetics - Items to add.
     */
    public Crate with(@Nonnull Cosmetics... cosmetics) {
        return (Crate) setContents(cosmetics);
    }

    /**
     * Adds all items with a given rarity to the contents of this crate.
     *
     * @param rarity  - Rarity.
     * @param exclude - Exclude cosmetics.
     */
    public Crate with(@Nonnull Rarity rarity, @Nullable Cosmetics... exclude) {
        for (Cosmetics value : Cosmetics.values()) {
            final Cosmetic cosmetic = value.getCosmetic();
            if (cosmetic.isExclusive() || cosmetic.getRarity() == Rarity.UNSET || value.isIgnore()) {
                continue;
            }

            if (value.getRarity() == rarity && !excludes(value, exclude)) {
                with(value);
            }
        }

        return this;
    }

    /**
     * Returns true if player owns all the items from this crate.
     *
     * @param player - Player to check.
     */
    public boolean isOwnAllItems(@Nonnull Player player) {
        for (Cosmetics cosmetics : getContents().listAll()) {
            if (!cosmetics.isUnlocked(player)) {
                return false;
            }
        }

        return true;
    }

    private boolean excludes(Cosmetics cosmetic, Cosmetics... excludes) {
        if (cosmetic == null || excludes == null) {
            return false;
        }

        for (Cosmetics exclude : excludes) {
            if (exclude == cosmetic) {
                return true;
            }
        }

        return false;
    }
}
