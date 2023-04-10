package me.hapyl.fight.game;

import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * This is used as a base for all game instances.
 * {@link GameInstance} implements this class.
 *
 * Whenever manager requests a GameInstance, it will
 * return either a valid GameInstance, or {@link #NULL_GAME_INSTANCE}.
 *
 * Null game instance is an empty GameInstance base.
 *
 * In reality, if {@link #NULL_GAME_INSTANCE} is returned,
 * the developer is doing something wrong. But it's better
 * than catching a null pointer.
 */
public interface IGameInstance {

    /**
     * Default GameInstance if failed to retrieve existing one.
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
     * Returns GamePlayer instance of a player, or null if player doesn't exist.
     *
     * @param player - Player.
     * @return GamePlayer instance of a player, or null if player doesn't exist.
     */
    @Nullable
    GamePlayer getPlayer(Player player);

    /**
     * Returns GamePlayer instance of a player, or null if player doesn't exist.
     *
     * @param uuid - Player's UUID.
     * @return GamePlayer instance of a player, or null if player doesn't exist.
     */
    @Nullable
    GamePlayer getPlayer(UUID uuid);

    /**
     * Returns map of players mapped to their UUID.
     *
     * @return Map of players mapped to their UUID.
     */
    @Nonnull
    Map<UUID, GamePlayer> getPlayers();

    /**
     * Returns all alive players with specifier hero selected.
     *
     * @param heroes - Filter.
     * @return All alive players with specifier hero selected.
     */
    @Nonnull
    List<GamePlayer> getAlivePlayers(Heroes heroes);

    /**
     * Returns all alive players.
     *
     * @return All alive players.
     */
    @Nonnull
    List<GamePlayer> getAlivePlayers();

    /**
     * Returns all alive players as bukkit player.
     *
     * @return All alive players as bukkit player.
     */
    @Nonnull
    List<Player> getAlivePlayersAsPlayers();

    /**
     * Returns all alive players who match the predicate.
     *
     * @param predicate - Predicate to match.
     * @return All alive players who match the predicate.
     */
    @Nonnull
    List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate);

    /**
     * Returns all alive players as bukkit player who match the predicate.
     *
     * @param predicate - Predicate to match.
     * @return All alive players as bukkit player who match the predicate.
     */
    @Nonnull
    List<Player> getAlivePlayersAsPlayers(Predicate<GamePlayer> predicate);

    /**
     * Returns a list of heroes that are used in the game.
     *
     * @return A list of heroes that are used in the game.
     */
    @Nonnull
    Set<Heroes> getActiveHeroes();

    /**
     * Forced game to check for win condition <b>and</b> stop and game if check passed.
     */
    void checkWinCondition();

    /**
     * Returns this instance game mode.
     *
     * @return This instance game mode.
     */
    @Nonnull
    CFGameMode getMode();

    /**
     * Returns this instance game mode as enum.
     *
     * @return This instance game mode as enum.
     */
    @Nonnull
    Modes getCurrentMode();

    /**
     * Returns true if player is winner.
     *
     * @param player - Player to check.
     * @return True if player is winner.
     */
    boolean isWinner(Player player);

    /**
     * Returns this instance map.
     *
     * @return This instance map.
     */
    @Nonnull
    GameMaps getMap();

    /**
     * Returns a task that is running for this game instance.
     *
     * @return A task that is running for this game instance.
     */
    @Nullable
    GameTask getGameTask();

    /**
     * Returns HEX code of this game instance.
     *
     * @return HEX code of this game instance.
     */
    @Nonnull
    String hexCode();

    /**
     * Returns all players, no matter if they're alive, dead, online etc.
     *
     * @return All players, no matter if they're alive, dead, online etc.
     */
    @Nonnull
    Collection<GamePlayer> getAllPlayers();

    /**
     * Returns true if this is a real GameInstance, false otherwise.
     */
    boolean isReal();
}
