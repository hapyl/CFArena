package me.hapyl.fight.game.attribute;

import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.game.attribute.temper.Temper;

import javax.annotation.Nonnull;

/**
 * Represents a {@link me.hapyl.fight.util.WeakCopy} of the {@link EntityAttributes}.
 * <p>
 * <b>Special Cases:</b>
 * <ul>
 *     <li>A weak copy does not call updates nor triggers.</li>
 *     <li>Adding or removing {@link Temper} is not supported!</li>
 * </ul>
 */
public class WeakEntityAttributes extends EntityAttributes {
    WeakEntityAttributes(EntityAttributes attributes) {
        super(attributes.getEntity(), attributes.getBaseAttributes());

        this.mapped.putAll(attributes.mapped);
        this.tempers.putAll(attributes.tempers);
    }

    @Override
    public void set(@Nonnull AttributeType type, double value) {
        mapped.put(type, value);
    }

    @Override
    @SuppressWarnings("deprecation")
    public double add(@Nonnull AttributeType type, double value) {
        final double newValue = getRaw(type) + value;

        mapped.put(type, newValue);
        return newValue;
    }

    @Override
    public double subtract(@Nonnull AttributeType type, double value) {
        return add(type, -value);
    }

    // *=* Deprecated *=* //

    @Deprecated(forRemoval = true)
    @Override
    public double subtractSilent(@Nonnull AttributeType type, double value) {
        throw makeError("subtractSilent");
    }

    @Deprecated(forRemoval = true)
    @Override
    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration) {
        throw makeError("increaseTemporary");
    }

    @Deprecated(forRemoval = true)
    @Override
    public Tuple<Double, Double> addSilent(@Nonnull AttributeType type, double value) {
        throw makeError("addSilent");
    }

    @Deprecated(forRemoval = true)
    @Override
    public void triggerUpdate(@Nonnull AttributeType type) {
        throw makeError("triggerUpdate");
    }

    private IllegalArgumentException makeError(String name) {
        return new IllegalArgumentException("Cannot use " + name + " in " + getClass().getSimpleName() + "!");
    }
}
