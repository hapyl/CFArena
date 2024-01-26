package me.hapyl.fight.event;

import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.WeakEntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Disposable;

import javax.annotation.Nonnull;

/**
 * Represents entity data for the {@link DamageInstance}.
 * <p>
 * The data makes a {@link me.hapyl.fight.util.WeakCopy} of entity {@link EntityAttributes}, meaning you are free to modify {@link #attributes} of this data.
 */
public class InstanceEntityData implements Disposable {

    public final LivingGameEntity entity;
    public final WeakEntityAttributes attributes;

    public InstanceEntityData(LivingGameEntity entity) {
        this.entity = entity;
        this.attributes = entity.getAttributes().weakCopy();
    }

    /**
     * Gets the entity associated with the data.
     *
     * @return the entity.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    /**
     * Gets the {@link me.hapyl.fight.util.WeakCopy} of entity's {@link EntityAttributes}.
     * You are <b>free</b> to modify this attributes since they're <b>not</b> linked to the actual attributes.
     *
     * @return the weak copy of entity's attributes.
     */
    @Nonnull
    public WeakEntityAttributes getAttributes() {
        return attributes;
    }

    /**
     * Disposes of this data.
     */
    @Override
    public void dispose() {
        attributes.dispose();
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
