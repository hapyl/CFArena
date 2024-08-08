package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.inventory.gui.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemActionPair {

    private final ItemStack itemStack;
    private final Action action;

    public ItemActionPair(@Nonnull ItemStack itemStack, @Nullable Action action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nullable
    public Action getAction() {
        return action;
    }
}
