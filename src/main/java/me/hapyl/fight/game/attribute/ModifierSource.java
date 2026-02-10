package me.hapyl.fight.game.attribute;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents the source of an attribute modifier, used as a key in a mapping of
 * {@code Map<ModifierSource, AttributeModifier>}.
 * <p>
 * Each instance is uniquely identified by a {@link Key} and can optionally specify whether its application should be silent.
 */
public final class ModifierSource implements Keyed {
    
    public static final ModifierSource COMMAND = new ModifierSource(Key.ofString("command"));
    
    private final Key key;
    private final boolean silent;
    
    public ModifierSource(@Nonnull Key key) {
        this(key, false);
    }
    
    public ModifierSource(@Nonnull Key key, boolean silent) {
        this.key = key;
        this.silent = silent;
    }
    
    @Nonnull
    @Override
    public Key getKey() {
        return key;
    }
    
    
    public boolean silent() {
        return silent;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        ModifierSource that = (ModifierSource) o;
        return Objects.equals(this.key, that.key);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
