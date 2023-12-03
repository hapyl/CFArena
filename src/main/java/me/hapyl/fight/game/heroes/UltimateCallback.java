package me.hapyl.fight.game.heroes;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Use this callback at {@link Hero#castUltimate(Player)} to execute code
 * after the casting is done. Used to remove local instances of entities, blocks etc.
 */
public interface UltimateCallback {

    /**
     * Code to execute before using the ultimate.
     *
     * @param player - Player, who uses the ultimate.
     */
    void callback(@Nonnull Player player);

}
