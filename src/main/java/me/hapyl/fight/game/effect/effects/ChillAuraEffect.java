package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ChillAuraEffect extends Effect {
    public ChillAuraEffect() {
        super("Chill Aura", Type.NEGATIVE);

        setDescription("""
                Chills enemies out.
                """);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.SLOWNESS, 0);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.SLOWNESS);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % 10 != 0) {
            return;
        }

        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.SNOWFLAKE, 5, 0.1d, 0.1d, 0.1d, 0.05f);
    }
}
