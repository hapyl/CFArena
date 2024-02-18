package me.hapyl.fight.game;

import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.maps.GameMaps;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class NullGameInstance implements IGameInstance {
    @Nonnull
    @Override
    public State getGameState() {
        return State.POST_GAME;
    }

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

    @Override
    public boolean checkWinCondition() {
        return false;
    }

    @Nonnull
    @Override
    public CFGameMode getMode() {
        return Modes.FFA.getMode();
    }

    @Override
    public boolean isWinner(Player player) {
        return false;
    }

    @Nonnull
    @Override
    public GameMaps getEnumMap() {
        return GameMaps.ARENA;
    }

    @Nonnull
    @Override
    public String hexCode() {
        return "fake_instance";
    }

    @Override
    public boolean isReal() {
        return false; // make sure this is false
    }
}
