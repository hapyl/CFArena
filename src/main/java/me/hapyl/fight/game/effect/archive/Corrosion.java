package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class Corrosion extends GameEffect {

    public final int DAMAGE_PERIOD = 10;

    public Corrosion() {
        super("Corrosion");
        this.setDescription("Slows, disturbs vision and rapidly damages players.");
        this.setPositive(false);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
        if (tick % DAMAGE_PERIOD == 0) {
            GamePlayer.damageEntityTick(entity, 1.0d, null, EnumDamageCause.CORROSION, DAMAGE_PERIOD);
        }
    }

    @Override
    public void onStart(LivingEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(999999, 4));
        entity.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(999999, 4));
    }

    @Override
    public void onStop(LivingEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.BLINDNESS);
    }

}
