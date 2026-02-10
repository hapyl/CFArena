package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.setting.EnumSetting;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class TalkBehaviour {
    
    private static final String format = "&e[HERO] &6%s&f: %s";
    
    private final Hero hero;
    
    private Sound sound;
    private float pitch;
    
    TalkBehaviour(@Nonnull Hero hero) {
        this.hero = hero;
        this.sound = Sound.ENTITY_VILLAGER_AMBIENT;
        this.pitch = 1.0f;
    }
    
    public void talk(@Nonnull GamePlayer player, @Nonnull String message) {
        if (player.isSettingEnabled(EnumSetting.MUTE_HERO_MESSAGES)) {
            return;
        }
        
        player.sendMessage(format.formatted(hero.getName(), message));
        player.playSound(sound, pitch);
    }
    
    @SelfReturn
    public TalkBehaviour sound(@Nonnull Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
        return this;
    }
}
