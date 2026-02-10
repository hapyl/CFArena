package me.hapyl.fight.gui.styled;

import me.hapyl.fight.Message;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.SoundEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface Styled {
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
    
    void setPanelItem(int index, @Nonnull ItemStack item, @Nullable Consumer<Player> action, @Nullable ClickType... clickTypes);
    
    void fillRow(int row, @Nonnull ItemStack item);
    
    default boolean isSetCloseButton() {
        return true;
    }
    
    default boolean isCanOpen(@Nonnull Player player) {
        if (Manager.current().isGameInProgress()) {
            Message.error(player, "You cannot open this GUI in a game!");
            Message.sound(player, SoundEffect.ERROR);
            return false;
        }
        
        if (this instanceof Disabled disabled) {
            disabled.errorMessage(player, "GUI");
            return false;
        }
        
        return true;
    }
    
}
