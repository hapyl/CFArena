package me.hapyl.fight.game.weapons;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface RightClickable {

    @Nonnull
    default String getRightClickName() {
        return "";
    }

    @Nonnull
    default String getRightClickDescription() {
        return "";
    }

    /**
     * Called upon player right-clicking with a weapon.
     *
     * @param player - Player.
     * @param item   - Item.
     */
    void onRightClick(@Nonnull GamePlayer player, @Nonnull ItemStack item);

}
