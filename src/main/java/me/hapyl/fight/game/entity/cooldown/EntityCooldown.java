package me.hapyl.fight.game.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Keep in mind cooldowns are in <b>millis, <i>not ticks</i></b>!
 */
public class EntityCooldown implements Keyed {
    
    private final Key key;
    private final long durationInMilliseconds;
    
    public EntityCooldown(@Nonnull Key key, long durationInMilliseconds) {
        this.key = key;
        this.durationInMilliseconds = durationInMilliseconds;
    }
    
    @Nonnull
    public static EntityCooldown of(@Nonnull String key, long durationInMilliseconds) {
        return new EntityCooldown(Key.ofString(key), durationInMilliseconds);
    }
    
    @Nonnull
    public static EntityCooldown of(@Nonnull String key) {
        return of(key, 0L);
    }
    
    @Nonnull
    @Override
    public final Key getKey() {
        return this.key;
    }
    
    public long durationInMilliseconds() {
        return durationInMilliseconds;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final EntityCooldown that = (EntityCooldown) o;
        return Objects.equals(this.key, that.key);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
}
