package me.hapyl.fight.game;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.talents.LoreAppender;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Represents a class that can create an ItemStack.
 */
public interface NonNullItemCreator extends LoreAppender {

    @Override
    default void appendLore(@Nonnull ItemBuilder builder) {
    }

    /**
     * Gets the {@link ItemStack} produces by this {@link NonNullItemCreator}.
     *
     * @return the {@link ItemStack}.
     * @implNote <b>must</b> set call {@link #createItem()} if the item is {@code null}
     * <br>
     * <pre>{@code
     *  private ItemStack item;
     *
     *  public ItemStack getItem() {
     *      if (item == null) {
     *          item = createItem();
     *      }
     *
     *      return item;
     *  }
     * }</pre>
     */
    @Nonnull
    ItemStack getItem();

    /**
     * Construct an {@link ItemStack}.
     * <br>
     *
     * @implNote This always returns a clean copy of the {@link ItemStack}, {@link #getItem()} must call and assign a reference.
     */
    @Nonnull
    ItemStack createItem();

}
