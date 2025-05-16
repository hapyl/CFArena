package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.NamedColoredPrefixed;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@AutoRegisteredListener
public abstract class Effect implements Keyed, NamedColoredPrefixed, Described {
    
    private final Key key;
    private final String prefix;
    private final String name;
    private final Color color;
    private final Type type;
    
    private String description;
    
    Effect(@Nonnull Key key, @Nonnull String prefix, @Nonnull String name, @Nonnull Color color, @Nonnull Type type) {
        this.key = key;
        this.prefix = prefix;
        this.name = name;
        this.color = color;
        this.type = type;
        this.description = "";
        
        AutoRegisteredListener.Handler.register(this);
    }
    
    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final Effect that = (Effect) o;
        return Objects.equals(this.key, that.key);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(key);
    }
    
    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }
    
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
    
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }
    
    public void setDescription(@Nonnull String description, @Nullable Object... objects) {
        setDescription(description.formatted(objects));
    }
    
    @Nonnull
    public Type getType() {
        return type;
    }
    
    @Nonnull
    @Override
    public String toString() {
        return toString0();
    }
    
    @Nonnull
    @Override
    public String toString(@Nullable String prefix, @Nullable String suffix) {
        return prefix + toString0() + suffix;
    }
    
    /**
     * Called once upon entity gaining this effect.
     *
     * @param effect - The active effect instance.
     */
    @EventLike
    public abstract void onStart(@Nonnull ActiveEffect effect);
    
    /**
     * Called once upon entity losing this effect.
     *
     * @param effect - The active effect instance.
     */
    @EventLike
    public abstract void onStop(@Nonnull ActiveEffect effect);
    
    /**
     * Called every tick entity has this effect.
     *
     * @param effect - The active effect instance.
     */
    @EventLike
    public void onTick(@Nonnull ActiveEffect effect) {
    }
    
    /**
     * Called whenever this effect has added to the entity when it already had the effect.
     *
     * @param effect - The active effect instance.
     */
    @EventLike
    public void onUpdate(@Nonnull ActiveEffect effect) {
    }
    
    public boolean shouldRemove(@Nonnull ActiveEffect effect) {
        return false;
    }
    
}
