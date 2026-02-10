package me.hapyl.fight.game.entity;

import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public record SoundEffect(@Nonnull Sound sound, float pitch) {
    
    public static final SoundEffect ERROR = of(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
    public static final SoundEffect FAILURE = of(Sound.ENTITY_VILLAGER_NO, 1.0f);
    public static final SoundEffect SUCCESS = of(Sound.ENTITY_VILLAGER_YES, 1.0f);
    public static final SoundEffect PURCHASE = of(Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
    
    public void play(@Nonnull GamePlayer player) {
        player.playSound(this);
    }
    
    public void play(@Nonnull Player player) {
        PlayerLib.playSound(player, sound, pitch);
    }
    
    @Nonnull
    public static SoundEffect of(@Nonnull Sound sound) {
        return of(sound, 1.0f);
    }
    
    @Nonnull
    public static SoundEffect of(@Nonnull Sound sound, float pitch) {
        return new SoundEffect(sound, pitch);
    }
}
