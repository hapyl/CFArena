package me.hapyl.fight.gui.styled;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.inventory.gui.Action;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class StyledGUI extends PlayerGUI implements Styled {

    private final Size size;

    public StyledGUI(Player player, String name, Size size) {
        super(player, name, size.size);

        this.size = size;
        StaticStyledGUI.openFx(this);
    }

    @Nonnull
    @Override
    public Size getStyleSize() {
        return size;
    }

    @Override
    public final void openInventory() {
        if (checkCanOpen(player)) {
            return;
        }

        clearItems();
        clearClickEvents();

        StaticStyledGUI.updateInventory(this);
        super.openInventory();
    }

    @Override
    public final void update() {
        openInventory();
    }

    @Override
    public void setHeader(@Nonnull ItemStack item) {
        StaticStyledGUI.setHeader(this, item);
    }

    @Override
    public void setPanelItem(int index, @Nonnull ItemStack item, @Nullable Action action, @Nullable ClickType... clickTypes) {
        StaticStyledGUI.setPanelItem(this, index, item, action, clickTypes);
    }

    @Override
    public void fillRow(int row, @Nonnull ItemStack item) {
        StaticStyledGUI.fillRow(this, row, item);
    }

}
