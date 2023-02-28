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

    public void setGameState(State gameState) {

    }

    public State getGameState() {
        return State.PRE_GAME;
    }

    public void calculateEverything() {

    }

    public void spawnFireworks(boolean flag) {

    }

    public long getTimeLeftRaw() {
        return 0;
    }

    public long getTimeLeft() {
        return 0;
    }

    public boolean isTimeIsUp() {
        return false;
    }

    @Nullable
    public GamePlayer getPlayer(Player player) {
        return null;
    }

    @Nullable
    public GamePlayer getPlayer(UUID uuid) {
        return null;
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return new HashMap<>();
    }

    public List<GamePlayer> getAlivePlayers(Heroes heroes) {
        return new ArrayList<>();
    }

    @Nonnull
    public List<GamePlayer> getAlivePlayers() {
        return new ArrayList<>();
    }

    public List<Player> getAlivePlayersAsPlayers() {
        return Lists.newArrayList();
    }

    public List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
        return getAlivePlayers();
    }

    public List<Player> getAlivePlayersAsPlayers(Predicate<GamePlayer> predicate) {
        return Lists.newArrayList();
    }

    public void checkWinCondition() {

    }

    public CFGameMode getMode() {
        return Modes.FFA.getMode();
    }

    public Modes getCurrentMode() {
        return Modes.FFA;
    }

    public boolean isWinner(Player player) {
        return false;
    }

    public GameMaps getCurrentMap() {
        return GameMaps.ARENA;
    }

    @Nullable
    public GameTask getGameTask() {
        return null;
    }

    public String hexCode() {
        return "null";
    }

}
