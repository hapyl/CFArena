package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class Riptide extends GameEffect {
    private final EffectParticle[] particles = new EffectParticle[2];

    public Riptide() {
        super("Riptide");
        this.setPositive(false);

        particles[0] = new EffectParticle(Particle.WATER_SPLASH, 3, 0.15d, 0.15d, 0.15d, 0.01f);
        particles[1] = new EffectParticle(Particle.GLOW, 1, 0.15d, 0.15d, 0.15d, 0.025f);
    }

    @Override
    public void onStart(LivingEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(999999, 0));
        entity.addPotionEffect(PotionEffectType.SPEED.createEffect(999999, 0));

        PlayerLib.playSound(Sound.AMBIENT_UNDERWATER_ENTER, 1.25f);
    }

    @Override
    public void onStop(LivingEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.SPEED);

        PlayerLib.playSound(Sound.AMBIENT_UNDERWATER_ENTER, 1.75f);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
        if (tick == 10) {
            for (final EffectParticle particle : particles) {
                displayParticles(entity.getEyeLocation().add(0.0d, 0.5d, 0.0d), entity, particle);
            }
        }
    }
}
