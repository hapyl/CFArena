package me.hapyl.fight.event.custom;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CustomEvent extends Event {

    /**
     * Calls this event and returns {@code true} if the event is {@link Cancellable} and {@link Cancellable#isCancelled()},
     * {@code false} otherwise.
     *
     * @return {@code true} if the event is {@link Cancellable} and {@link Cancellable#isCancelled()}, {@code false} otherwise.
     */
    public final boolean call() {
        Bukkit.getPluginManager().callEvent(this);

        if (this instanceof Cancellable cancellable) {
            return cancellable.isCancelled();
        }

        return false;
    }

    @Override
    @Deprecated(forRemoval = true)
    public boolean callEvent() {
        throw new IllegalStateException("paper callEvent is dogshit use #call");
    }
}
