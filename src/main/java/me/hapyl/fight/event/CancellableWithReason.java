package me.hapyl.fight.event;

import org.bukkit.event.Cancellable;

import javax.annotation.Nonnull;

public interface CancellableWithReason extends Cancellable {

    default void setCancelled(boolean cancel, @Nonnull String reason) {
        setCancelled(cancel);
        setReason(reason);
    }

    @Nonnull
    String getReason();

    void setReason(@Nonnull String reason);

}
