package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class SlowingAuraEffect extends Effect {

    public final int cooldownModifier = 2;

    public SlowingAuraEffect() {
        super("Slowing Aura", EffectType.NEGATIVE);

        setDescription("Slows players and increases talent cooldowns.");
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.SLOWNESS, 1);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.SLOWNESS);
    }
}
