package me.hapyl.fight.game.heroes;

import me.hapyl.fight.annotate.AutomaticallyHandled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.collection.player.PlayerDataMap;

import javax.annotation.Nonnull;

public interface PlayerDataHandler<T extends PlayerData> {

    /**
     * Gets the {@link PlayerDataMap} for this handler.
     *
     * @return player data.
     */
    @Nonnull
    PlayerDataMap<T> getDataMap();

    /**
     * Gets the data for the given player.
     * <p>
     * If overridden, the data <b>must</b> be computed if absent.
     *
     * @param player - Player.
     * @return the data for the given player.
     */
    @Nonnull
    default T getPlayerData(@Nonnull GamePlayer player) {
        final PlayerDataMap<T> dataMap = getDataMap();

        return dataMap.computeIfAbsent(player, fn -> dataMap.newData(player));
    }

    /**
     * Gets the data for the given player.
     * <br>
     * This method returns null if the data is not present.
     *
     * @param player - Player.
     * @return the data or null.
     */
    @Nonnull
    default T getPlayerDataOrNull(@Nonnull GamePlayer player) {
        return getDataMap().get(player);
    }

    /**
     * Removes the data for the given player from the map and call {@link PlayerData#remove()}.
     * <br>
     * <b>This is automatically called upon {@link GamePlayer} "death" if player's {@link Hero} implements this interface.</b>
     *
     * @param player - Player.
     */
    @AutomaticallyHandled(in = GamePlayer.class)
    default void removePlayerData(@Nonnull GamePlayer player) {
        getDataMap().removeAnd(player, T::remove);
    }

    /**
     * Remove the data for all players and calls {@link PlayerData#remove()}.
     * <br>
     * <b>This is automatically called for each {@link Hero} if it implements this interface.</b>
     */
    @AutomaticallyHandled(in = GameInstance.class)
    default void resetPlayerData() {
        getDataMap().forEachAndClear(T::remove);
    }
}
