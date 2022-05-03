package kz.hapyl.fight.game;

import kz.hapyl.fight.game.gamemode.CFGameMode;
import kz.hapyl.fight.game.gamemode.Modes;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractGameInstance {

    public static final AbstractGameInstance NULL_GAME_INSTANCE = new AbstractGameInstance() {

        /**
         * This will indicate the the invalid instance.
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

    public String formatWinnerName(GamePlayer gp) {
        return gp.getPlayer().getName();
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

    public List<GamePlayer> getAlivePlayers() {
        return new ArrayList<>();
    }

    public List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
        return getAlivePlayers();
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

    public Set<GamePlayer> getWinners() {
        return new HashSet<>();
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
