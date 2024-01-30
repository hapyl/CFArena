package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.PlayerDataHandler;

import javax.annotation.Nonnull;

/**
 * Used in {@link PlayerDataHandler}.
 */
public abstract class PlayerDataMap<T extends PlayerData> extends PlayerHashMap<T> {

    /**
     * Creates a new {@link PlayerData}.
     * @param player - Player.
     * @return a new player data.
     */
    @Nonnull
    public abstract T newData(@Nonnull GamePlayer player);

}
