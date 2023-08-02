package me.hapyl.fight.trigger.subscribe;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.trigger.EntityTrigger;

public class AttributeChangeTrigger extends EntityTrigger {

    /**
     * Type of the changed attribute.
     */
    public final AttributeType type;
    /**
     * Old value of the attribute.
     */
    public final double oldValue;
    /**
     * New value of the attribute.
     */
    public final double newValue;

    public AttributeChangeTrigger(LivingGameEntity entity, AttributeType type, double oldValue, double newValue) {
        super(entity);
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "AttributeChangeTrigger{" +
                "player=" + entity +
                ", type=" + type +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }

}
