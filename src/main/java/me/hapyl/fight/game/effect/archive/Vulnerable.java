package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

public class Vulnerable extends GameEffect {

    public Vulnerable() {
        super("Vulnerable");
        setDescription("Players affected by vulnerability take 50%% more damage.");
        setPositive(false);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
        if (tick == 5) {
            displayParticles(entity.getEyeLocation().add(0.0d, 0.5d, 0.0d), entity, new EffectParticle(Particle.VILLAGER_ANGRY, 1));
        }
    }

    @Override
    public void onStart(LivingEntity entity) {

    }

    @Override
    public void onStop(LivingEntity entity) {

    }
}
