package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.LivingGameEntity;

public abstract class GameEntityEvent extends CustomEvent {

    protected final LivingGameEntity entity;

    public GameEntityEvent(LivingGameEntity entity) {
        this.entity = entity;
    }

    public LivingGameEntity getEntity() {
        return entity;
    }
}
