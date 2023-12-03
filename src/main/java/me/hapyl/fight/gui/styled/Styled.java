package me.hapyl.fight.gui.styled;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Styled {
    void onUpdate();

    @Nonnull
    Size getStyleSize();

    @Nullable
    default ReturnData getReturnData() {
        return null;
    }

    void update();

    void setHeader(@Nonnull ItemStack item);

    default void setPanelItem(int index, @Nonnull ItemStack item) {
        setPanelItem(index, item, null);
    }

    void setPanelItem(int index, @Nonnull ItemStack item, @Nullable me.hapyl.spigotutils.module.inventory.gui.Action action);

    void fillRow(int row, @Nonnull ItemStack item);
}
