package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

public class ChillAuraEffect extends GameEffect {
    public ChillAuraEffect() {
        super("Chill Aura");
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW, 20000, 0);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
        if (tick % 10 != 0) {
            return;
        }

        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.SNOWFLAKE, 3, 0.1d, 0.1d, 0.1d, 0.05f);
    }
}
