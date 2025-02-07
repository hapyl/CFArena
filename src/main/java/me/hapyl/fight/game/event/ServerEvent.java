package me.hapyl.fight.game.event;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.CF;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public abstract class ServerEvent implements Described, Listener {

    private final String name;
    private final String description;

    protected ServerEvent(String name, String description) {
        this.name = name;
        this.description = description;

        CF.registerEvents(this);
    }

    public abstract boolean isActive();

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
}
