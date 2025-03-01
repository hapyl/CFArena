package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ParachuteEffect extends Effect {
    public ParachuteEffect() {
        super("Parachute", Type.POSITIVE);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (entity.isOnGround()) {
            entity.removeEffect(EffectType.PARACHUTE);
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffect(PotionEffectType.SLOW_FALLING, 0, PotionEffect.INFINITE_DURATION);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.SLOW_FALLING);
    }
}
