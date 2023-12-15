package me.hapyl.fight.game.heroes.archive.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MadnessEffect {

    private final String suffix;
    private final PotionEffect effect;
    private final boolean isPositive;
    protected final int duration;

    public MadnessEffect(@Nonnull String suffix, @Nullable PotionEffectType type, int durationSec, int level) {
        this.suffix = suffix;
        this.effect = type == null ? null : new PotionEffect(type, durationSec * 20, level);
        this.duration = durationSec;
        this.isPositive = durationSec == 30;
    }

    public MadnessEffect(@Nonnull String suffix, int durationSec) {
        this(suffix, null, durationSec, 0);
    }

    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
    }

    @Nonnull
    public String getSuffix() {
        return suffix;
    }

    public void applyEffects(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
        affect(player, victim);

        if (effect != null) {
            victim.addPotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier());
        }

        victim.sendMessage(isPositive ?
                "&a&l☘ &aAlchemical Madness %s&a!".formatted(suffix) :
                "&c☠ Alchemical Madness %s&c!".formatted(suffix));
    }

}
