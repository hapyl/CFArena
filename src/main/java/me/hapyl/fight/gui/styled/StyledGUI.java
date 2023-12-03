package me.hapyl.fight.gui.styled;

import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
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
    public void setPanelItem(int index, @Nonnull ItemStack item, @Nullable me.hapyl.spigotutils.module.inventory.gui.Action action) {
        StaticStyledGUI.setPanelItem(this, index, item, action);
    }

    @Override
    public void fillRow(int row, @Nonnull ItemStack item) {
        StaticStyledGUI.fillRow(this, row, item);
    }

}
