package me.hapyl.fight.event.custom;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CustomEvent extends Event {
    
    /**
     * Calls this event and returns {@code true} if the event was cancelled, {@code false} otherwise.
     * <p>This method always returns {@code false} if the event is not {@link Cancellable}.</p>
     *
     * @return {@code true} if the event was cancelled, {@code false} otherwise.
     */
    @Override
    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        
        return this instanceof Cancellable cancellable && cancellable.isCancelled();
    }
}
