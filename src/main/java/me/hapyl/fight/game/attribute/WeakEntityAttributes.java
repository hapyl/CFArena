package me.hapyl.fight.game.attribute;

/**
 * Represents a {@link me.hapyl.fight.util.WeakCopy} of the {@link EntityAttributes}.
 */
public class WeakEntityAttributes extends EntityAttributes {
    WeakEntityAttributes(EntityAttributes attributes) {
        super(attributes.getEntity(), attributes.getBaseAttributes());

        this.mapped.putAll(attributes.mapped);
        this.tempers.putAll(attributes.tempers);
    }
}
