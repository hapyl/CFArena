package me.hapyl.fight.game;

import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * This is used as a base for all game instances.
 * {@link GameInstance} implements this class.
 * <p>
 * Whenever manager requests a GameInstance, it will
 * return either a valid GameInstance, or {@link #NULL_GAME_INSTANCE}.
 * <p>
 * Null game instance is an empty GameInstance base.
 * <p>
 * In reality, if {@link #NULL_GAME_INSTANCE} is returned,
 * the developer is doing something wrong. But it's better
 * than catching a null pointer.
 */
public interface IGameInstance {

    /**
     * Default GameInstance if failed to retrieve the existing one.
     * Should never happen unless unsafe call was made.
     */
    IGameInstance NULL_GAME_INSTANCE = new NullGameInstance();

    /**
     * Returns the current game state.
     *
     * @return Current game state.
     */
    @Nonnull
    State getGameState();

    /**
     * Changes current game state.
     *
     * @param gameState - New game state.
     */
    void setGameState(State gameState);

    /**
     * Calculate game instance and awards winners.
     */
    void calculateEverything();

    /**
     * Returns raw time left in millis.
     *
     * @return Raw time left in millis.
     */
    long getTimeLeftRaw();

    /**
     * Returns time left in ticks.
     *
     * @return Time left in ticks.
     */
    long getTimeLeft();

    /**
     * Returns true if time surpasses the limit.
     *
     * @return - True if time is up, false otherwise.
     */
    boolean isTimeIsUp();

    /**
     * Forced game to check for win condition <b>and</b> stop and game if check passed.
     *
     * @return
     */
    boolean checkWinCondition();

    /**
     * Returns this instance game mode.
     *
     * @return This instance game mode.
     */
    @Nonnull
    CFGameMode getMode();

    /**
     * Returns true if player is winner.
     *
     * @param player - Player to check.
     * @return True if player is a winner.
     */
    boolean isWinner(Player player);

    /**
     * Returns this instance maps.
     *
     * @return This instance map.
     */
    @Nonnull
    GameMaps getEnumMap();

    /**
     * Returns HEX code of this game instance.
     *
     * @return HEX code of this game instance.
     */
    @Nonnull
    String hexCode();

    /**
     * Returns true if this is a real GameInstance, false otherwise.
     */
    boolean isReal();

    /**
     * Returns total number of kills in this instance as of now.
     *
     * @return total number of kills in this instance as of now.
     */
    default int getTotalKills() {
        int kills = 0;

        for (GameTeam team : GameTeam.values()) {
            kills += team.data.kills;
        }

        return kills;
    }

    /**
     * Returns total number of deaths in this instance as of now.
     *
     * @return total number of deaths in this instance as of now.
     */
    default int getTotalDeaths() {
        int deaths = 0;

        for (GameTeam team : GameTeam.values()) {
            deaths += team.data.deaths;
        }

        return deaths;
    }

}
