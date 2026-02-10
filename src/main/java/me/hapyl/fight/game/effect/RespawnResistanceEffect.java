package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public class RespawnResistanceEffect extends Effect {
    
    RespawnResistanceEffect(Key key) {
        super(key, "🛡", "Respawn Resistance", Color.ROYAL_PURPLE, Type.POSITIVE);
        
        setDescription("""
                       Negates all damage for a short period after respawning.
                       """);
        
        // particle = new EffectParticle(Particle.ENCHANTED_HIT, 5, 0.25, 0.5, 0.25, 0.1f);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().setInvulnerable(true);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        effect.entity().setInvulnerable(false);
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        if (effect.tick == 5) {
            // particle.display(entity.getMidpointLocation(), entity);
        }
    }
}
