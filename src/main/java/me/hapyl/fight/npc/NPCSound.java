package me.hapyl.fight.npc;

import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class NPCSound {

    private Sound sound;
    private float pitch;

    public NPCSound() {
        this.sound = Sound.ENTITY_VILLAGER_YES;
        this.pitch = 1.0f;
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
