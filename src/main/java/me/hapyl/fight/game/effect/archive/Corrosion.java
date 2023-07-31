package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.potion.PotionEffectType;

public class Corrosion extends GameEffect {

    public final int DAMAGE_PERIOD = 10;

    public Corrosion() {
        super("Corrosion");
        this.setDescription("Slows, disturbs vision and rapidly damages players.");
        this.setPositive(false);
    }

    @Override
    public void onTick(GameEntity entity, int tick) {
        if (tick % DAMAGE_PERIOD == 0) {
            entity.damageTick(1.0d, EnumDamageCause.CORROSION, DAMAGE_PERIOD);
        }
    }

    @Override
    public void onStart(GameEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW, 999999, 4);
        entity.addPotionEffect(PotionEffectType.BLINDNESS, 999999, 4);
    }

    @Override
    public void onStop(GameEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.BLINDNESS);
    }

}
