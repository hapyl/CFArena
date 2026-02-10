package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

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
            Sound.AMBIENT_CAVE
    };
    
    ParanoiaEffect(Key key) {
        super(key, "\uD83E\uDEE5", "Paranoia", Color.PALE_TEAL, Type.NEGATIVE);
        
        setDescription("""
                       Blinds and plays decoy sounds.
                       """);
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // Plays a sound every 20 ticks or with 10% chance
        if (effect.tick % 20 == 0 || entity.random.checkBound(0.1)) {
            // Get a random location to play decoy sound
            final Location location = entity.getLocation();
            location.add(random.nextDouble(-10, 10), 0, random.nextDouble(-10, 10));
            
            if (random.nextFloat() < 0.6 && !location.getBlock().getType().isAir()) {
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
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().addPotionEffectIndefinitely(PotionEffectType.DARKNESS, 1);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        // This needed for smooth fade-out
        entity.removePotionEffect(PotionEffectType.DARKNESS);
        entity.addPotionEffect(PotionEffectType.BLINDNESS, 1, 20);
    }
}
