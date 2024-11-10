package me.hapyl.fight.npc;

import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PersistentNPCSound {

    private final Sound sound;
    private final float pitch;

    public PersistentNPCSound() {
        this(Sound.ENTITY_VILLAGER_YES, 1.0f);
    }

    public PersistentNPCSound(float pitch) {
        this(Sound.ENTITY_VILLAGER_YES, pitch);
    }

    public PersistentNPCSound(@Nonnull Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }

    public void play(@Nonnull Player player) {
        PlayerLib.playSound(player, sound, pitch);
    }

    @Nonnull
    public Sound getSound() {
        return sound;
    }

    public float getPitch() {
        return pitch;
    }

}
