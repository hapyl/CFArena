package me.hapyl.fight.gui.styled;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.Action;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.inventory.gui.StrictAction;
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

    protected void setItemRanked(int slot, @Nonnull ItemBuilder builder, @Nonnull PlayerRank rank, @Nonnull String actionString, @Nonnull Action action) {
        final boolean hasRank = rank.isOrHigher(player);
        final String prefix = rank.getPrefixWithFallback();

        builder.addLore();

        if (hasRank) {
            builder.addLore(Color.BUTTON + actionString);
        }
        else {
            builder.addLore(
                    Color.ERROR_DARKER + "You must be %s to use this!".formatted(prefix + Color.ERROR_DARKER)
            );
        }

        setItem(slot, builder.asIcon(), new StrictAction() {
            @Override
            public void onLeftClick(@Nonnull Player player) {
                if (!hasRank) {
                    Notifier.Error.NOT_PERMISSIONS_NEED_RANK.send(player, prefix);
                    Notifier.sound(player, SoundEffect.ERROR);
                    return;
                }

                action.invoke(player);
            }
        });
    }
}
