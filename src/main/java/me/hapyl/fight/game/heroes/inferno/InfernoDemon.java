package me.hapyl.fight.game.heroes.inferno;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;

import javax.annotation.Nonnull;

public final class InfernoDemon {

    private final GamePlayer player;
    private final InfernoDemonType type;
    private final LivingGameEntity entity;

    private final int transformAt;
    private final int duration;

    public InfernoDemon(@Nonnull GamePlayer player, @Nonnull InfernoDemonType type, int duration, @Nonnull LivingGameEntity entity) {
        this.player = player;
        this.type = type;
        this.entity = entity;
        this.transformAt = player.aliveTicks();
        this.duration = duration;
    }

    @Nonnull
    public String getTimeLeft() {
        return CFUtils.formatTick(duration - (player.aliveTicks() - transformAt));
    }

    @Nonnull
    public GamePlayer player() {
        return player;
    }

    @Nonnull
    public InfernoDemonType type() {
        return type;
    }

    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }

}
