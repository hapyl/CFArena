package me.hapyl.fight.game.heroes.archive.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {

    private final String suffix;
    private final PotionEffect effect;
    private final boolean isPositive;

    public void affect(GamePlayer player, GamePlayer victim) {
    }

    public Effect(String suffix, PotionEffectType type, int duration, int level) {
        this.suffix = suffix;
        this.effect = type == null ? null : new PotionEffect(type, duration * 20, level);
        this.isPositive = duration == 30;
    }

    public String getSuffix() {
        return suffix;
    }

    public Effect(String suffix, int duration) {
        this(suffix, null, duration, 0);
    }

    public void applyEffects(GamePlayer player, GamePlayer victim) {
        this.affect(player, victim);

        if (effect != null) {
            victim.addPotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier());
        }

        victim.sendMessage(isPositive ?
                "&a&l☘ &aAlchemical Madness %s&a!".formatted(suffix) :
                "&c☠ Alchemical Madness %s&c!".formatted(suffix));
    }

}
