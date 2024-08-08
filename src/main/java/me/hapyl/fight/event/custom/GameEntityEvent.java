package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public abstract class GameEntityEvent extends CustomEvent {

    protected final LivingGameEntity entity;

    public GameEntityEvent(LivingGameEntity entity) {
        this.entity = entity;
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }
}
