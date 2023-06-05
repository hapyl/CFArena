package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.PotionGameEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class WitherBlood extends PotionGameEffect {
    public WitherBlood() {
        super("Withered Blood");
        setDescription("Feel the wither in your blood.");
        setPositive(false);

        setPotionEffect(PotionEffectType.WITHER, 1);
        setPotionEffect(PotionEffectType.BLINDNESS, 1);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
    }

    @Override
    public void onStartAfter(@Nonnull LivingEntity entity) {

    }

    @Override
    public void onStopAfter(@Nonnull LivingEntity entity) {

    }
}
