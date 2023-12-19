package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface PlayerDataHandler {

    /**
     * Returns existing player data.
     * <p>
     * If player has no data, the method <b>must</b> compute the data.
     *
     * @param player - Player.
     * @return existing player data.
     */
    @Nonnull
    PlayerData getPlayerData(@Nonnull GamePlayer player);

}
