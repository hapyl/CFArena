package me.hapyl.fight.game.weapons;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface LeftClickable {

    /**
     * Called upon player left-clicking with a weapon.
     *
     * @param player - Player.
     * @param item   - Item.
     */
    void onLeftClick(@Nonnull GamePlayer player, @Nonnull ItemStack item);

}
