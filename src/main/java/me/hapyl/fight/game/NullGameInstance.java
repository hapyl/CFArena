package me.hapyl.fight.game;

import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class NullGameInstance implements IGameInstance {
    @Override
    public void setGameState(State gameState) {

    }

    @Override
    public void calculateEverything() {

    }

    @Override
    public long getTimeLeftRaw() {
        return 0;
    }

    @Override
    public long getTimeLeft() {
        return 0;
    }

    @Override
    public boolean isTimeIsUp() {
        return false;
    }

    @Nullable
    @Override
    public GamePlayer getPlayer(Player player) {
        return null;
    }

    @Nullable
    @Override
    public GamePlayer getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public void checkWinCondition() {

    }

    @Nonnull
    @Override
    public CFGameMode getMode() {
        return Modes.FFA.getMode();
    }

    @Nonnull
    @Override
    public Modes getCurrentMode() {
        return Modes.FFA;
    }

    @Override
    public boolean isWinner(Player player) {
        return false;
    }

    @Nonnull
    @Override
    public GameMaps getCurrentMap() {
        return GameMaps.ARENA;
    }

    @Nullable
    @Override
    public GameTask getGameTask() {
        return null;
    }

    @Nonnull
    @Override
    public String hexCode() {
        return "fake_instance";
    }

    @Nonnull
    @Override
    public Collection<GamePlayer> getAllPlayers() {
        return new HashSet<>();
    }

    @Override
    public boolean isReal() {
        return false; // make sure this is false
    }
}
