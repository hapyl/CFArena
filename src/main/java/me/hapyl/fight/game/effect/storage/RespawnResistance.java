package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

public class RespawnResistance extends GameEffect {

    public RespawnResistance() {
        super("Respawn Resistance");
        setEffectParticle(new EffectParticle(Particle.CRIT_MAGIC, 5, 0.25d, 0.5d, 0.25d, 0.1f));
    }

    @Override
    public void onStart(LivingEntity entity) {
        entity.setInvulnerable(true);
    }

    @Override
    public void onStop(LivingEntity entity) {
        entity.setInvulnerable(false);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
        if (tick == 5) {
            displayParticles(entity.getLocation(), entity);
        }
    }
}
