package me.hapyl.fight.event;

import org.bukkit.event.Cancellable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CancellableBy extends Cancellable {
    
    @Nullable
    CancelSource source();
    
    @Override
    default boolean isCancelled() {
        return source() != null;
    }
    
    @Override
    @Deprecated
    default void setCancelled(boolean cancel) {
        setCancelled(cancel ? () -> "Anonymous" : null);
    }
    
    void setCancelled(@Nullable CancelSource source);
    
    interface CancelSource {
        @Nonnull
        String identifier();
    }
}
