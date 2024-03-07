package me.hapyl.fight.game.event;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public abstract class ServerEvent implements Described {

    private final String name;
    private final String description;

    protected ServerEvent(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract boolean isActive();

    @Event
    public void onJoin(@Nonnull PlayerProfile profile) {
    }

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
