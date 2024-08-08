package me.hapyl.fight.game.entity;

import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum SoundEffect {

    ERROR(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f),
    FAILURE(Sound.ENTITY_VILLAGER_NO, 1.0f),
    SUCCESS(Sound.ENTITY_VILLAGER_YES, 1.0f),
    PURCHASE(Sound.ENTITY_PLAYER_LEVELUP, 2.0f),

    ;

    private final Sound sound;
    private final float pitch;

    SoundEffect(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }

    public void play(@Nonnull GamePlayer player) {
        play(player.getPlayer());
    }

    @Super
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
