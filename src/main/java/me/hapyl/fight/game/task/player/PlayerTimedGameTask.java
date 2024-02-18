package me.hapyl.fight.game.task.player;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.game.task.TimedGameTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PlayerTimedGameTask extends TimedGameTask implements IPlayerTask {

    private final GamePlayer player;
    private final Enum<?> name;

    public PlayerTimedGameTask(@Nonnull GamePlayer player, @Nonnull Timed timed) {
        this(player, null, timed.getDuration());
    }

    public PlayerTimedGameTask(@Nonnull GamePlayer player, int maxTick) {
        this(player, null, maxTick, 0);
    }

    public PlayerTimedGameTask(@Nonnull GamePlayer player, @Nullable Enum<?> name, @Nonnull Timed timed) {
        this(player, name, timed.getDuration());
    }

    public PlayerTimedGameTask(@Nonnull GamePlayer player, @Nullable Enum<?> name, int maxTick) {
        this(player, name, maxTick, 0);
    }

    public PlayerTimedGameTask(@Nonnull GamePlayer player, @Nullable Enum<?> name, int maxTick, int initialTick) {
        super(maxTick, initialTick);

        this.player = player;
        this.name = name;

        player.addTask(this);
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
