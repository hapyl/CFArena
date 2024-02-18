package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Riptide extends Effect {

    public Riptide() {
        super("Riptide", EffectType.NEGATIVE);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.SLOW, 0);
        entity.addPotionEffectIndefinitely(PotionEffectType.SPEED, 0);

        entity.playSound(Sound.AMBIENT_UNDERWATER_ENTER, 1.25f);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.SPEED);

        entity.playSound(Sound.AMBIENT_UNDERWATER_ENTER, 1.75f);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
    }
}
