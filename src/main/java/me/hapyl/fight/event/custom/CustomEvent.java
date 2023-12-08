package me.hapyl.fight.event.custom;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CustomEvent extends Event {

    public final void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    /**
     * Returns true if the event was cancelled.
     *
     * @return true if the event was cancelled.
     */
    public final boolean callAndCheck() {
        call();

        if (this instanceof Cancellable cancellable) {
            return cancellable.isCancelled();
        }

        return false;
    }

}
