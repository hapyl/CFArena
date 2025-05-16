package me.hapyl.fight.game;

import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface NamedColoredPrefixed extends Named {
    
    /**
     * Gets the prefix of this object.
     *
     * @return the prefix of this object.
     */
    @Nonnull
    String getPrefix();
    
    /**
     * Gets the prefix of this object prefixed by the color.
     *
     * @return the prefix of this object prefixed by the color.
     */
    @Nonnull
    default String getPrefixColored() {
        return getColor() + getPrefix();
    }
    
    @Nonnull
    default String getPrefixColoredBukkit() {
        return getColor().backingColor + getPrefix();
    }
    
    /**
     * Gets the name of this object.
     *
     * @return the name of this object.
     */
    @Nonnull
    @Override
    String getName();
    
    /**
     * Gets the name of this object prefixed by the color.
     *
     * @return the name of this object prefixed by the color.
     */
    @Nonnull
    default String getNameColored() {
        return getColor() + getName();
    }
    
    @Nonnull
    default String getNameColoredBukkit() {
        return getColor().backingColor + getName();
    }
    
    /**
     * Gets the color of this object.
     *
     * @return the color of this object.
     */
    @Nonnull
    Color getColor();
    
    /**
     * @implNote Due to {@link Object} limitation, an object must implement their own way of string representation.
     * <p>The suggested implementation is as follows:</p>
     * <pre>{@code
     *     @Nonnull
     *     @Override
     *     public String toString() {
     *         return toString0();
     *     }
     * }</pre>
     */
    @Nonnull
    String toString();
    
    /**
     * A helper method acting as {@link Object#toString()} due to {@link Object} limitations.
     */
    @Nonnull
    default String toString0() {
        return "%s %s".formatted(getPrefixColored(), getColor() + getName()) + Constants.DEFAULT_LORE_COLOR;
    }
    
    /**
     * Gets the string representation of this object, prefixed and suffixed with the given prefix and suffix.
     *
     * @param prefix - The prefix.
     * @param suffix - The suffix.
     * @return the string representation of this object, prefixed and suffixed with the given prefix and suffix.
     */
    @Nonnull
    default String toString(@Nullable String prefix, @Nullable String suffix) {
        return prefix + this + suffix;
    }
    
    
    /**
     * Prefixes the given object with this object's colored prefix.
     *
     * @param object - The object to prefix.
     * @return the given object with this object's colored prefix.
     */
    @Nonnull
    default String prefix(@Nonnull Object object) {
        return "%s %s".formatted(getPrefixColored(), object);
    }
    
    /**
     * Suffixes the given object with this object's colored prefix.
     *
     * @param object - The object to prefix.
     * @return the given object with this object's colored prefix.
     */
    @Nonnull
    default String suffix(@Nonnull Object object) {
        return "%s%s %s".formatted(getColor(), object, getPrefixColored());
    }
    
}
