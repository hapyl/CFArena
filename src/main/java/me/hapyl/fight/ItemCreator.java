package me.hapyl.fight;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Represents a simple {@link ItemStack} creator.
 *
 * <p>The implementation is required to implement the {@link #createBuilder()} method that will <b>always</b> return a new builder, but {@link #createItem()} may be
 * overridden to cache the {@link ItemStack} via lazy initiation to save on resources:
 * <pre>{@code
 *     private ItemStack cachedItem; // Should generally not be exposed
 *
 *     @Nonnull
 *     @Override
 *     public ItemStack createItem() {
 *         if (cachedItem == null) {
 *             cachedItem = createBuilder().build();
 *         }
 *
 *         return cachedItem;
 *     }
 * }</pre>
 *
 * <p>Keep in mind caching the {@link ItemStack} makes it mutable be anyone that calls {@link #createItem()}.
 */
public interface ItemCreator {
    
    /**
     * Gets the {@link ItemStack}.
     *
     * @return the {@link ItemStack}.
     * @implNote Implementation may, but not required to, cache the built {@link ItemStack} leaving it vulnerable to mutations.
     */
    @Nonnull
    default ItemStack createItem() {
        return createBuilder().build();
    }
    
    /**
     * Creates a new instance of {@link ItemBuilder}.
     *
     * @return a new instance of {@link ItemBuilder}.
     */
    @Nonnull
    ItemBuilder createBuilder();
    
}
