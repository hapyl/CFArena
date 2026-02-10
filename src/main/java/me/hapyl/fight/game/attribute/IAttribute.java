package me.hapyl.fight.game.attribute;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;

/**
 * Represents an adapter-like interface between {@link Attribute} and {@link AttributeType}, where the former implements
 * the methods and the latter references those methods, as example:
 * <pre>{@code
 * class Attribute implements IAttribute {
 *      @Override
 *      public double defaultValue() {
 *          return 100;
 *      }
 * }
 *
 * enum AttributeType implements IAttribute {
 *     MAX_HEALTH(new Attribute());
 *
 *     Attribute attribute;
 *
 *     AttributeType(Attribute attribute) {
 *         this.attribute = attribute;
 *     }
 *
 *     @Override
 *     public double defaultValue() {
 *         return attribute.defaultValue();
 *     }
 * }
 * }</pre>
 */
public interface IAttribute extends Described {
    
    /**
     * Gets the default value for this attribute.
     * <p>Default values are written upon {@link BaseAttributes} initiation.</p>
     *
     * @return the default value for this attribute.
     */
    double defaultValue();
    
    /**
     * Gets the minimum value for this attribute.
     * <p>Attribute value is clamped and cannot be lower than the minimum value.</p>
     *
     * @return the minimum value for this attribute.
     * @see BaseAttributes#get(AttributeType)
     */
    double minValue();
    
    /**
     * Gets the maximum value for this attribute
     * <p>Attribute value is clamped and cannot be higher than the maximum value.</p>
     *
     * @return the maximum value for this attribute.
     * @see BaseAttributes#get(AttributeType)
     */
    double maxValue();
    
    /**
     * Clamps the given value between {@link #minValue()} and {@link #maxValue()}.
     *
     * @param value - The value to clamp.
     * @return the clamped value.
     */
    default double clamp(double value) {
        return Math.clamp(value, minValue(), maxValue());
    }
    
    /**
     * Gets whether the new value is considered a buff.
     *
     * @param newValue - The new value.
     * @param oldValue - The old value.
     * @return {@code true} if the new value is considered a buff, {@code false} otherwise.
     */
    default boolean isBuff(double newValue, double oldValue) {
        return newValue > oldValue;
    }
    
    /**
     * Gets the name for this attribute.
     *
     * @return the name for this attribute.
     */
    @Nonnull
    @Override
    String getName();
    
    /**
     * Gets the description for this attribute.
     *
     * @return the description for this attribute.
     */
    @Nonnull
    @Override
    String getDescription();
    
    /**
     * Gets the character prefix for this attribute.
     *
     * @return the character prefix for this attribute.
     */
    @Nonnull
    String getCharacter();
    
    /**
     * Gets the color associated with this attribute.
     *
     * @return the color associated with this attribute.
     */
    @Nonnull
    Color getColor();
    
    /**
     * Gets the string representation of the attribute.
     * <p>This is designed to be used as a reference in string context,
     * and the default implementation includes colored character and name.</p>
     *
     * @return this attribute to a string representation.
     * @see AttributeType#getFormatted(BaseAttributes)
     * @see AttributeType#getFormattedWithAttributeName(BaseAttributes)
     */
    @Nonnull
    String toString();
    
    /**
     * Gets the string representation of the value based on this attribute {@link AttributeFormat}.
     *
     * @param value - The value to format.
     * @return the string representation of the value based on this attribute {@link AttributeFormat}.
     * @see AttributeType#getFormatted(BaseAttributes)
     * @see AttributeType#getFormattedWithAttributeName(BaseAttributes)
     */
    @Nonnull
    String toString(double value);
    
}
