package me.hapyl.fight.event;

import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;

public abstract class ProfileEvent extends PlayerEvent {

    private final PlayerProfile profile;

    ProfileEvent(PlayerProfile profile) {
        super(profile.getPlayer());
        this.profile = profile;
    }

    @Nonnull
    public PlayerProfile getProfile() {
        return profile;
    }
}
