package me.hapyl.fight.event;

import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called whenever {@link PlayerProfile} is created.
 */
public class ProfileInitializationEvent extends ProfileEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public ProfileInitializationEvent(@Nonnull PlayerProfile profile) {
        super(profile);
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
