package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * Use this callback at {@link Hero#castUltimate(GamePlayer)} to execute code
 * after the casting is done. Used to remove local instances of entities, blocks etc.
 */
public interface UltimateCallback {

    /**
     * Code to execute before using the ultimate.
     *
     * @param player - Player, who uses the ultimate.
     */
    void callback(@Nonnull GamePlayer player);

}
