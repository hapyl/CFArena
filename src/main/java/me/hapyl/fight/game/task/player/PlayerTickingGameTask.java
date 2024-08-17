package me.hapyl.fight.game.task.player;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PlayerTickingGameTask extends TickingGameTask implements IPlayerTask {

    protected final GamePlayer player;
    protected final Class<?> name;

    public PlayerTickingGameTask(@Nonnull GamePlayer player, @Nullable Class<?> name) {
        this.player = player;
        this.name = name;

        player.addTask(this);
    }

    public PlayerTickingGameTask(@Nonnull GamePlayer player) {
        this(player, null);
    }

    @Nonnull
    @Override
    public GamePlayer getPlayer() {
        return player;
    }

    @Nullable
    @Override
    public Class<?> getEnum() {
        return name;
    }
}
