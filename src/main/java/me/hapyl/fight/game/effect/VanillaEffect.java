package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class VanillaEffect extends Effect {

    private final PotionEffectType effect;

    public VanillaEffect(String name, PotionEffectType effect, EffectType type) {
        super(name, type);

        this.effect = effect;
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.getEntity().addPotionEffect(new org.bukkit.potion.PotionEffect(effect, INFINITE_DURATION, amplifier, false, false, false));
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.getEntity().removePotionEffect(effect);
    }

}
