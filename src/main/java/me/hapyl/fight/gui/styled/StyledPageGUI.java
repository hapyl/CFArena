package me.hapyl.fight.gui.styled;

import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.inventory.gui.PlayerPageGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class StyledPageGUI<T> extends PlayerPageGUI<T> implements Styled {

    private final Size size;

    public StyledPageGUI(Player player, String name, Size size) {
        super(player, name, size.size);

        this.size = size;
        StaticStyledGUI.openFx(this);
    }

    @Nonnull
    @Override
    public Size getStyleSize() {
        return size;
    }

    public final void update() {
        openInventory(1);
    }

    @Override
    public final void postProcessInventory(@Nonnull Player player, int page) {
        StaticStyledGUI.updateInventory(this);

        // Override page arrows
        if (page > 1) {
            setItem(
                    getSize() - 7,
                    StyledTexture.ARROW_LEFT.asIcon("&aPrevious Page", Color.BUTTON + "Click to open the previous page!"),
                    pl -> openInventory(page - 1)
            );
        }
        else if (page < getMaxPage()) {
            setItem(
                    getSize() - 3,
                    StyledTexture.ARROW_RIGHT.asIcon("&aNext Page", Color.BUTTON + "Click to open the next page!"),
                    pl -> openInventory(page + 1)
            );
        }
    }

    public void setHeader(@Nonnull ItemStack item) {
        StaticStyledGUI.setHeader(this, item);
    }

    public void setPanelItem(int index, @Nonnull ItemStack item, @Nullable me.hapyl.spigotutils.module.inventory.gui.Action action) {
        StaticStyledGUI.setPanelItem(this, index, item, action);
    }

    public void fillRow(int row, @Nonnull ItemStack item) {
        StaticStyledGUI.fillRow(this, row, item);
    }

}
