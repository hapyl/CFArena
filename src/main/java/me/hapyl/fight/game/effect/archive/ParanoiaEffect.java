package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ParanoiaEffect extends GameEffect {

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
        super("Paranoia");
        setDescription("Blinds players and plays decoy sounds around them.");
        setPositive(false);
        setEffectParticle(new EffectParticle(Particle.SQUID_INK, 5, 0.175d, 0.175d, 0.175d, 0.02f));
    }

    @Override
    public void onTick(GameEntity entity, int tick) {
        // Plays a sound every 20 ticks or with 10% chance
        if (tick == 0 || Math.random() >= 0.9d) {
            // Display paranoia for all players but the viewer
            final Location spawnLocation = entity.getLocation().clone().add(0, 1.7d, 0);
            this.displayParticles(spawnLocation, entity.getEntity());

            // Get a random location to play decoy sound
            Location location = entity.getLocation();
            location.add(ThreadRandom.nextDouble(-10, 10), 0, ThreadRandom.nextDouble(-10, 10));

            if (ThreadRandom.nextFloatAndCheckBetween(0.6f, 1.0f) && !location.getBlock().getType().isAir()) {
                final SoundGroup soundGroup = location.getBlock().getBlockData().getSoundGroup();

                if (entity instanceof Player player) {
                    player.playSound(location, soundGroup.getStepSound(), SoundCategory.MASTER, 1, 1);
                }
            }
            else {
                final Sound sound = CollectionUtils.randomElement(decoySounds, decoySounds[0]);

                if (entity instanceof Player player) {
                    player.playSound(location, sound, SoundCategory.MASTER, 1, sound == Sound.AMBIENT_CAVE ? 2 : 1);
                }
            }
        }
    }

    @Override
    public void onStart(GameEntity entity) {
        entity.addPotionEffect(PotionEffectType.DARKNESS.createEffect(99999, 1));
    }

    @Override
    public void onStop(GameEntity entity) {
        // This needed for smooth fade-out
        entity.removePotionEffect(PotionEffectType.DARKNESS);
        entity.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20, 1));
    }
}
