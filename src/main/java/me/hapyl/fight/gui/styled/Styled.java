package me.hapyl.fight.gui.styled;

import me.hapyl.fight.Notifier;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.SoundEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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

    void setPanelItem(int index, @Nonnull ItemStack item, @Nullable me.hapyl.eterna.module.inventory.gui.Action action, @Nullable ClickType... clickTypes);

    void fillRow(int row, @Nonnull ItemStack item);

    default boolean isSetCloseButton() {
        return true;
    }

    default boolean checkCanOpen(@Nonnull Player player) {
        if (Manager.current().isGameInProgress()) {
            Notifier.error(player, "You cannot open this GUI in a game!");
            Notifier.sound(player, SoundEffect.ERROR);
            return true;
        }

        return false;
    }

}
