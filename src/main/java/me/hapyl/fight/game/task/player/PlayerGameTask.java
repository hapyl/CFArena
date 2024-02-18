package me.hapyl.fight.game.task.player;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * GameTask that is automatically canceled if a player has died.
 */
public abstract class PlayerGameTask extends GameTask implements IPlayerTask {

    private final GamePlayer player;
    private final Enum<?> name;

    public PlayerGameTask(@Nonnull GamePlayer player, @Nullable Enum<?> name) {
        this.name = name;
        this.player = player;

        player.addTask(this);
    }

    public PlayerGameTask(@Nonnull GamePlayer player) {
        this(player, null);
    }

    @Nonnull
    @Override
    public GamePlayer getPlayer() {
        return player;
    }

    @Nullable
    @Override
    public Enum<?> getEnum() {
        return name;
    }

}
