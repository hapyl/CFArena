package me.hapyl.fight.npc;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class NPCSound {

    private Sound sound;
    private float pitch;

    public NPCSound() {
        this.sound = Sound.ENTITY_VILLAGER_YES;
        this.pitch = 1.0f;
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

    public void setSound(@Nonnull Sound sound) {
        this.sound = sound;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
