package me.hapyl.fight.game.task.player;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GeometryTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PlayerGeometryTask extends GeometryTask implements IPlayerTask {

    private final GamePlayer player;
    private final Enum<?> name;

    public PlayerGeometryTask(@Nonnull GamePlayer player, @Nullable Enum<?> name) {
        this.player = player;
        this.name = name;

        player.addTask(this);
    }

    public PlayerGeometryTask(@Nonnull GamePlayer player) {
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
