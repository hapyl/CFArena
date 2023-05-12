package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Vulnerable extends GameEffect {

    public Vulnerable() {
        super("Vulnerable");
        setDescription("Players affected by vulnerability take 50%% more damage.");
        setPositive(false);
    }

    @Override
    public void onTick(Player player, int tick) {
        if (tick == 5) {
            displayParticles(player.getEyeLocation().add(0.0d, 0.5d, 0.0d), player, new EffectParticle(Particle.VILLAGER_ANGRY, 1));
        }
    }

    @Override
    public void onStart(Player player) {

    }

    @Override
    public void onStop(Player player) {

    }
}
