package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Corrosion extends Effect {

    public final int damagePeriod = 10;

    public Corrosion() {
        super("Corrosion", Type.NEGATIVE);
        setDescription("""
                Slows, disturbs vision and rapidly deals damages.
                """);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % damagePeriod == 0) {
            entity.damage(1.0d, DamageCause.CORROSION);
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.SLOWNESS, 4);
        entity.addPotionEffectIndefinitely(PotionEffectType.BLINDNESS, 4);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.SLOWNESS);
        entity.removePotionEffect(PotionEffectType.BLINDNESS);
    }

}
