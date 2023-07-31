package me.hapyl.fight.trigger;

import me.hapyl.fight.game.entity.GameEntity;

public class EntityTrigger implements Trigger {

    public final GameEntity entity;

    public EntityTrigger(GameEntity player) {
        this.entity = player;
    }
}
