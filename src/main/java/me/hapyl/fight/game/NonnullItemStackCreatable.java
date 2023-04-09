package me.hapyl.fight.game;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Represents a class that can create an ItemStack.
 */
public abstract class NonnullItemStackCreatable {

    private ItemStack item;

    public final void setItem(@Nonnull ItemStack item) {
        if (this.item != null) {
            throw new IllegalStateException("Item already set!");
        }

        this.item = item;
    }

    public final ItemStack getItemUnsafe() throws IllegalStateException {
        if (this.item == null) {
            throw new IllegalStateException("unsafe");
        }

        return item;
    }

    @Nonnull
    public final ItemStack getItem() {
        if (item == null) {
            createItem();
        }

        if (item == null) {
            throw new IllegalStateException("Must implement createItem() and set item!");
        }

        return item;
    }

    public abstract void createItem();

}
