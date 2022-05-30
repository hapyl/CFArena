package me.hapyl.fight.util;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Allows to write ItemStack only once.
 */
@Deprecated
public class CachedItemStack extends Final<ItemStack> {

    public CachedItemStack() {
        this(null);
    }

    public CachedItemStack(@Nullable ItemStack stack) {
        super(stack);
    }

    public boolean cache(ItemStack stack) {
        return set(stack);
    }

    public boolean isCached() {
        return !isNull();
    }

    @Nullable
    public ItemStack getItem() {
        return get();
    }

    public ItemStack getItem(ItemStack def) {
        return isCached() ? get() : def;
    }

}
