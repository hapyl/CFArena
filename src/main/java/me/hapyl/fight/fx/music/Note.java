package me.hapyl.fight.fx.music;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Note {

    private final Sound sound;
    private final float pitch;

    public Note(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }

    /**
     * Plays the note at the given location for <b>all</b> players.
     *
     * @param location - Location to play at.
     */
    public final void play(@Nonnull Location location) {
        PlayerLib.playSound(location, sound, pitch);
    }

    /**
     * Plays the note for the given player at their location.
     *
     * @param player - Player to play note to.
     */
    public final void play(@Nonnull Player player) {
        play(player, player.getLocation());
    }

    /**
     * Plays the note for the given player at the given location.
     *
     * @param player   - Player to play note to.
     * @param location - Location to play note at.
     */
    public final void play(@Nonnull Player player, @Nonnull Location location) {
        PlayerLib.playSound(player, location, sound, pitch);
    }
}
