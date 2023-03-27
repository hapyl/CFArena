package me.hapyl.fight.game;

import com.google.common.collect.Lists;
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
 * To be honest, this class should really be named GameInstance, and
 * implementations should be named something like ActiveGameInstance,
 * but it's too late now.
 */
public abstract class AbstractGameInstance {

    public static final AbstractGameInstance NULL_GAME_INSTANCE = new AbstractGameInstance() {

        /**
         * This will indicate the invalid instance.
         */
        @Override
        public long getTimeLeft() {
            return Integer.MIN_VALUE;
        }
    };

    public AbstractGameInstance() {
    }

    /**
     * Changes current game state.
     *
     * @param gameState - New game state.
     */
    public void setGameState(State gameState) {
    }

    /**
     * Returns the current game state.
     *
     * @return Current game state.
     */
    public State getGameState() {
        return State.PRE_GAME;
    }

    /**
     * Calculate game instance and awards winners.
     */
    public void calculateEverything() {
    }

    /**
     * Returns raw time left in millis.
     *
     * @return Raw time left in millis.
     */
    public long getTimeLeftRaw() {
        return 0;
    }

    /**
     * Returns time left in ticks.
     *
     * @return Time left in ticks.
     */
    public long getTimeLeft() {
        return 0;
    }

    /**
     * Returns true if time surpasses the limit.
     *
     * @return - True if time is up, false otherwise.
     */
    public boolean isTimeIsUp() {
        return false;
    }

    /**
     * Returns GamePlayer instance of a player, or null if player doesn't exist.
     *
     * @param player - Player.
     * @return GamePlayer instance of a player, or null if player doesn't exist.
     */
    @Nullable
    public GamePlayer getPlayer(Player player) {
        return null;
    }

    /**
     * Returns GamePlayer instance of a player, or null if player doesn't exist.
     *
     * @param uuid - Player's UUID.
     * @return GamePlayer instance of a player, or null if player doesn't exist.
     */
    @Nullable
    public GamePlayer getPlayer(UUID uuid) {
        return null;
    }

    /**
     * Returns map of players mapped to their UUID.
     *
     * @return Map of players mapped to their UUID.
     */
    public Map<UUID, GamePlayer> getPlayers() {
        return new HashMap<>();
    }

    /**
     * Returns all alive players with specifier hero selected.
     *
     * @param heroes - Filter.
     * @return All alive players with specifier hero selected.
     */
    @Nonnull
    public List<GamePlayer> getAlivePlayers(Heroes heroes) {
        return new ArrayList<>();
    }

    /**
     * Returns all alive players.
     *
     * @return All alive players.
     */
    @Nonnull
    public List<GamePlayer> getAlivePlayers() {
        return new ArrayList<>();
    }

    /**
     * Returns all alive players as bukkit player.
     *
     * @return All alive players as bukkit player.
     */
    public List<Player> getAlivePlayersAsPlayers() {
        return Lists.newArrayList();
    }

    /**
     * Returns all alive players who match the predicate.
     *
     * @param predicate - Predicate to match.
     * @return All alive players who match the predicate.
     */
    public List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
        return getAlivePlayers();
    }

    /**
     * Returns all alive players as bukkit player who match the predicate.
     *
     * @param predicate - Predicate to match.
     * @return All alive players as bukkit player who match the predicate.
     */
    public List<Player> getAlivePlayersAsPlayers(Predicate<GamePlayer> predicate) {
        return Lists.newArrayList();
    }

    /**
     * Forced game to check for win condition <b>and</b> stop and game if check passed.
     */
    public void checkWinCondition() {
    }

    /**
     * Returns this instance game mode.
     *
     * @return This instance game mode.
     */
    public CFGameMode getMode() {
        return Modes.FFA.getMode();
    }

    /**
     * Returns this instance game mode as enum.
     *
     * @return This instance game mode as enum.
     */
    public Modes getCurrentMode() {
        return Modes.FFA;
    }

    /**
     * Returns true if player is winner.
     *
     * @param player - Player to check.
     * @return True if player is winner.
     */
    public boolean isWinner(Player player) {
        return false;
    }

    /**
     * Returns this instance map.
     *
     * @return This instance map.
     */
    public GameMaps getCurrentMap() {
        return GameMaps.ARENA;
    }

    /**
     * Returns a task that is running for this game instance.
     *
     * @return A task that is running for this game instance.
     */
    @Nullable
    public GameTask getGameTask() {
        return null;
    }

    /**
     * Returns HEX code of this game instance.
     *
     * @return HEX code of this game instance.
     */
    public String hexCode() {
        return "null";
    }

    /**
     * Returns all players, no matter if they're alive, dead, online etc.
     *
     * @return All players, no matter if they're alive, dead, online etc.
     */
    public Collection<GamePlayer> getAllPlayers() {
        return Lists.newArrayList();
    }

    /**
     * Returns true if this game is abstract (Not real).
     * If false, this game is real.
     *
     * @return True if this game is abstract (Not real), false otherwise.
     */
    public boolean isAbstract() {
        return true;
    }
}
