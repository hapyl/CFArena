package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ChillAuraEffect extends Effect {
    public ChillAuraEffect() {
        super("Chill Aura", EffectType.NEGATIVE);

        setDescription("""
                Chills enemies out.
                """);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.SLOW, 0);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % 10 != 0) {
            return;
        }

        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.SNOWFLAKE, 5, 0.1d, 0.1d, 0.1d, 0.05f);
    }
}
