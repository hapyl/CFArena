package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class LingerEffect extends Effect {
    LingerEffect(@Nonnull Key key) {
        super(key, "\uD83D\uDC3E", "Linger", Color.SILVER, Type.NEUTRAL);
        
        setDescription("""
                       Affected entity falls slowly.
                       """);
    }
    
    @Override
    public boolean shouldRemove(@Nonnull ActiveEffect effect) {
        return isOnGround(effect);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().addPotionEffect(PotionEffectType.SLOW_FALLING, 1, effect.duration());
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        effect.entity().removePotionEffect(PotionEffectType.SLOW_FALLING);
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        // Fx
        final double rad = Math.toRadians(effect.tick * 25);
        final double x = Math.sin(rad) * 0.5;
        final double z = Math.cos(rad) * 0.5;
        
        final LivingGameEntity entity = effect.entity();
        final Location location = entity.getLocation();
        
        LocationHelper.offset(
                location, x, 0, z, () -> entity.spawnWorldParticle(location, Particle.SNOWFLAKE, 2, 0.1, 0, 0.1, 0.02f)
        );
        
        LocationHelper.offset(
                location, -x, 0, -z, () -> entity.spawnWorldParticle(location, Particle.SNOWFLAKE, 2, 0.1, 0, 0.1, 0.02f)
        );
    }
    
    static boolean isOnGround(@Nonnull ActiveEffect effect) {
        return effect.duration() - effect.tick > 10 && effect.entity().isOnGround();
    }
}
