package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ParanoiaEffect extends Effect {

    private final Sound[] decoySounds = {
            Sound.ENTITY_PLAYER_HURT,
            Sound.ENTITY_PLAYER_BIG_FALL,
            Sound.ENTITY_PLAYER_SMALL_FALL,
            Sound.ENTITY_PLAYER_ATTACK_SWEEP,
            Sound.ENTITY_PLAYER_ATTACK_CRIT,
            Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK,
            Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,
            Sound.ENTITY_PLAYER_ATTACK_STRONG,
            Sound.ENTITY_PLAYER_ATTACK_WEAK,
            Sound.AMBIENT_CAVE,
    };

    public ParanoiaEffect() {
        super("Paranoia", EffectType.NEGATIVE);

        setDescription("""
                Blinds players and plays decoy sounds around them.
                """);

        setEffectParticle(new EffectParticle(Particle.SQUID_INK, 5, 0.175d, 0.175d, 0.175d, 0.02f));
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        // Plays a sound every 20 ticks or with 10% chance
        if (tick % 20 == 0 || Math.random() >= 0.9d) {
            // Display paranoia for all players but the viewer
            final Location spawnLocation = entity.getLocation().clone().add(0, 1.7d, 0);
            //this.displayParticles(spawnLocation, entity.getEntity()); Moved to omen debuff to only show the particles for the player

            // Get a random location to play decoy sound
            Location location = entity.getLocation();
            location.add(ThreadRandom.nextDouble(-10, 10), 0, ThreadRandom.nextDouble(-10, 10));

            if (ThreadRandom.nextFloatAndCheckBetween(0.6f, 1.0f) && !location.getBlock().getType().isAir()) {
                final SoundGroup soundGroup = location.getBlock().getBlockData().getSoundGroup();
                entity.playSound(location, soundGroup.getStepSound(), 1);
            }
            else {
                final Sound sound = CollectionUtils.randomElement(decoySounds, decoySounds[0]);
                entity.playSound(location, sound, sound == Sound.AMBIENT_CAVE ? 2 : 1);
            }
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.DARKNESS, 1);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        // This needed for smooth fade-out
        entity.removePotionEffect(PotionEffectType.DARKNESS);
        entity.addPotionEffect(PotionEffectType.BLINDNESS, 1, 20);
    }
}
