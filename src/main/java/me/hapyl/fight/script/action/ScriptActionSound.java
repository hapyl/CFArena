package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ScriptActionSound implements ScriptAction {

    private final Sound sound;
    private final float pitch;
    private final Location location;

    ScriptActionSound(@Nonnull Sound sound, float pitch, double x, double y, double z) {
        this.sound = sound;
        this.pitch = pitch;
        this.location = BukkitUtils.defLocation(x, y, z);
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        PlayerLib.playSound(location, sound, pitch);
    }

    @Override
    public String toString() {
        return "%s~%.1f".formatted(sound.name(), pitch);
    }
}
