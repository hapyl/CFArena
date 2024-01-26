package me.hapyl.fight.game;

import me.hapyl.fight.game.talents.LoreAppender;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Represents a class that can create an ItemStack.
 */
public abstract class NonNullItemCreator implements LoreAppender {

    protected ItemStack item;

    public final void setItem(@Nonnull ItemStack item) {
        if (this.item != null) {
            throw new IllegalStateException("Item already set!");
        }

        this.item = item;
    }

    @Override
    public void appendLore(@Nonnull ItemBuilder builder) {
    }

    public final ItemStack getItemUnsafe() throws IllegalStateException {
        if (this.item == null) {
            throw new IllegalStateException("getItemUnsafe()");
        }

        return item;
    }

    @Nonnull
    public final ItemStack getItem() {
        if (item == null) {
            item = createItem();
        }

        return item;
    }

    /**
     * A class must override and set item using this method.
     */
    @Nonnull
    public abstract ItemStack createItem();

}
