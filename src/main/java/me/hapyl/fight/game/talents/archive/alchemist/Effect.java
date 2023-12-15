package me.hapyl.fight.game.talents.archive.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Effect {

    private final String effectName;
    private final String effectChar;
    private final PotionEffect potionEffect;

    public Effect(@Nonnull String effectChar, @Nonnull String effectName) {
        this(effectChar, effectName, null, 0, 0);
    }

    public Effect(@Nonnull String effectChar, @Nonnull String effectName, @Nullable PotionEffectType effect, int duration, int level) {
        this.effectChar = effectChar;
        this.effectName = effectName;
        this.potionEffect = effect == null ? null : new PotionEffect(effect, duration, level);
    }

    public void affect(@Nonnull GamePlayer player) {
    }

    @Nonnull
    public String getChar() {
        return effectChar;
    }

    @Nonnull
    public String getName() {
        return effectName;
    }

    public void applyEffectsIgnoreFx(@Nonnull GamePlayer player) {
        affect(player);

        if (potionEffect != null) {
            player.addPotionEffect(potionEffect.getType(), potionEffect.getDuration(), potionEffect.getAmplifier());
        }
    }

    public void applyEffects(@Nonnull GamePlayer player) {
        applyEffectsIgnoreFx(player);

        // Fx
        player.playWorldSound(Sound.ENTITY_PLAYER_SWIM, 1.8f);
        player.sendTitle("&a" + effectChar, "&6Gained " + effectName, 5, 10, 5);
        player.sendMessage("&a" + effectChar + " &6Gained " + effectName);
    }

}
