package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Corrosion extends GameEffect {

    public final int DAMAGE_PERIOD = 10;

    public Corrosion() {
        super("Corrosion");
        this.setDescription("Slows, disturbs vision and rapidly damages players.");
        this.setPositive(false);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % DAMAGE_PERIOD == 0) {
            entity.damageTick(1.0d, EnumDamageCause.CORROSION, DAMAGE_PERIOD);
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW, 999999, 4);
        entity.addPotionEffect(PotionEffectType.BLINDNESS, 999999, 4);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.BLINDNESS);
    }

}
