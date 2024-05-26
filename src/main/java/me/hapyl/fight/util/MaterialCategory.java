package me.hapyl.fight.util;

import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum MaterialCategory {

    /**
     * {@link Material#isItem()}.
     */
    ITEM {
        @Override
        boolean match(Material material) {
            return material.isItem();
        }
    },
    /**
     * {@link Material#isBlock()}.
     */
    BLOCK {
        @Override
        boolean match(Material material) {
            return material.isBlock();
        }
    },
    /**
     * {@link Material#isOccluding()}.
     */
    OCCLUDING() {
        @Override
        boolean match(Material material) {
            return material.isOccluding();
        }
    };

    boolean match(Material material) {
        return false;
    }

    /**
     * Gets all {@link MaterialCategory} matching the given material.
     *
     * @param material - Material.
     * @return set of categories that is matching the given material.
     */
    @Nullable
    public static MaterialCategory of(@Nonnull Material material) {
        for (MaterialCategory category : values()) {
            if (category.match(material)) {
                return category;
            }
        }

        return null;
    }

}