package me.hapyl.fight.game.talents.archive.alchemist;

import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Effect {

    private final String effectName;
    private final String effectChar;
    private final Effects effect;
    private final int amplifier;
    private final int duration;

    public Effect(@Nonnull String effectChar, @Nonnull String effectName) {
        this(effectChar, effectName, null, 0, 0);
    }

    public Effect(@Nonnull String effectChar, @Nonnull String effectName, @Nullable Effects effect, int amplifier, int duration) {
        this.effectChar = effectChar;
        this.effectName = effectName;
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
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

        if (effect != null) {
            player.addEffect(effect, amplifier, duration);
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
