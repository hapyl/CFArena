package me.hapyl.fight.game.task;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class PlayerTask extends GameTask {

    private final GamePlayer player;

    public PlayerTask(@Nonnull Player player) {
        this(getGamePlayer(player));
    }

    public PlayerTask(@Nonnull GamePlayer player) {
        this.player = player;
    }

    public abstract void run(@Nonnull GamePlayer player);

    @Override
    public final void run() {
        if (!player.isAlive()) {
            cancelIfActive();
            return;
        }

        run(player);
    }

    @Nonnull
    private static GamePlayer getGamePlayer(Player player) {
        final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

        if (gamePlayer == null) {
            throw new IllegalStateException("do not start PlayerTask outside a game!");
        }

        return gamePlayer;
    }

}
