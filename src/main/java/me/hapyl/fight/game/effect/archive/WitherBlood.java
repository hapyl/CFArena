package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class WitherBlood extends Effect {
    public WitherBlood() {
        super("Withered Blood", EffectType.NEGATIVE);

        setDescription("Feel the wither in your blood.");
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.WITHER, 1);
        entity.addPotionEffectIndefinitely(PotionEffectType.BLINDNESS, 1);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.WITHER);
        entity.removePotionEffect(PotionEffectType.BLINDNESS);
    }
}
