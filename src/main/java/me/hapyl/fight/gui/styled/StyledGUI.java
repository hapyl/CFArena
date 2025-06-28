package me.hapyl.fight.gui.styled;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.inventory.gui.StrictAction;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.SoundEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.function.Consumer;

public abstract class StyledGUI extends PlayerGUI implements Styled {

    private final Size size;

    public StyledGUI(@Nonnull Player player, @Nonnull String name, @Nonnull Size size) {
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
    @OverridingMethodsMustInvokeSuper
    public void onUpdate() {
        StaticStyledGUI.updateInventory(this);
    }
    
    @Override
    public final void openInventory() {
        if (!isCanOpen(player)) {
            return;
        }
        
        super.openInventory();
    }

    @Override
    public final void update() {
        // TODO @Jun 12, 2025 (xanyjl) -> diff on open/close inv
        openInventory();
    }

    @Override
    public void setHeader(@Nonnull ItemStack item) {
        StaticStyledGUI.setHeader(this, item);
    }

    @Override
    public void setPanelItem(int index, @Nonnull ItemStack item, @Nullable Consumer<Player> action, @Nullable ClickType... clickTypes) {
        StaticStyledGUI.setPanelItem(this, index, item, action, clickTypes);
    }

    @Override
    public void fillRow(int row, @Nonnull ItemStack item) {
        StaticStyledGUI.fillRow(this, row, item);
    }

    protected void setItemRanked(int slot, @Nonnull ItemBuilder builder, @Nonnull PlayerRank rank, @Nonnull String actionString, @Nonnull Consumer<Player> action) {
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
                    Message.Error.NOT_PERMISSIONS_NEED_RANK.send(player, prefix);
                    Message.sound(player, SoundEffect.ERROR);
                    return;
                }

                action.accept(player);
            }
        });
    }
}
