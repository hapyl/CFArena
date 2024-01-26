package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class RespawnResistance extends Effect {

    public RespawnResistance() {
        super("Respawn Resistance", EffectType.POSITIVE);

        setDescription("""
                Negates all damage.
                """);

        setEffectParticle(new EffectParticle(Particle.CRIT_MAGIC, 5, 0.25d, 0.5d, 0.25d, 0.1f));
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.setInvulnerable(true);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.setInvulnerable(false);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick == 5) {
            displayParticles(entity.getLocation(), entity);
        }
    }
}
