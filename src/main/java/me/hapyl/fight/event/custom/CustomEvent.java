package me.hapyl.fight.event.custom;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CustomEvent extends Event {

    public final void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    public final boolean callAndCheck() {
        call();

        if (this instanceof Cancellable cancellable) {
            return cancellable.isCancelled();
        }

        return false;
    }

}
