package me.hapyl.fight.trigger;

import me.hapyl.fight.game.entity.LivingGameEntity;

public class EntityTrigger implements Trigger {

    public final LivingGameEntity entity;

    public EntityTrigger(LivingGameEntity player) {
        this.entity = player;
    }
}
