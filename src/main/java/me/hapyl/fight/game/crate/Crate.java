package me.hapyl.fight.game.crate;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.*;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Crate extends RandomLoot<Cosmetic> implements Described {

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

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    public Cosmetic firstItem() {
        return getContents().firstItem();
    }

    @Nonnull
    public Color firstItemColor() {
        final Cosmetic item = firstItem();

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
        final ItemContents<Cosmetic> contents = getContents();

        for (BelongsToCollection item : collection.getItems()) {
            if (item instanceof Cosmetic enumCosmetic && enumCosmetic.isNotDisabledNorExclusive()) {
                contents.addItem(enumCosmetic);
            }
        }

        updateSchema();
        return this;
    }

    public Crate with(@Nonnull Type type) {
        final ItemContents<Cosmetic> contents = getContents();

        for (Cosmetic cosmetic : Registries.cosmetics().values()) {
            if (cosmetic.isNotDisabledNorExclusive() && cosmetic.getType() == type) {
                contents.addItem(cosmetic);
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
    public Crate with(@Nonnull Cosmetic... cosmetics) {
        return (Crate) setContents(cosmetics);
    }

    /**
     * Adds all items with a given rarity to the contents of this crate.
     *
     * @param rarity  - Rarity.
     * @param exclude - Exclude cosmetics.
     */
    public Crate with(@Nonnull Rarity rarity, @Nullable Cosmetic... exclude) {
        for (Cosmetic value : Registries.cosmetics().values()) {
            if (!value.isNotDisabledNorExclusive()) {
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
        for (Cosmetic enumCosmetic : getContents().listAll()) {
            if (!enumCosmetic.isUnlocked(player)) {
                return false;
            }
        }

        return true;
    }

    private boolean excludes(Cosmetic cosmetic, Cosmetic... excludes) {
        if (cosmetic == null || excludes == null) {
            return false;
        }

        for (Cosmetic exclude : excludes) {
            if (exclude == cosmetic) {
                return true;
            }
        }

        return false;
    }
}
