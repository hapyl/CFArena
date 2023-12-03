package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;

public class Vulnerable extends GameEffect {

    public Vulnerable() {
        super("Vulnerable");
        setDescription("Players affected by vulnerability take 50%% more damage.");
        setPositive(false);
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
        if (tick == 5) {
            displayParticles(
                    entity.getEyeLocation().add(0.0d, 0.5d, 0.0d),
                    entity.getEntity(),
                    new EffectParticle(Particle.VILLAGER_ANGRY, 1)
            );
        }
    }

    @Override
    public void onStart(LivingGameEntity entity) {

    }

    @Override
    public void onStop(LivingGameEntity entity) {

    }
}
