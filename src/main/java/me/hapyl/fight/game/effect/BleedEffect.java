package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class BleedEffect extends Effect implements Listener {
    
    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            Color.fromRGB(125, 1, 20),
            Color.fromRGB(194, 14, 41),
            2
    );
    
    private final int damagePeriod = 15;
    private final double damage = 0.01; // % of max health
    
    BleedEffect(Key key) {
        super(key, "∲", "Bleeding", me.hapyl.fight.game.color.Color.BLOOD_RED, Type.NEGATIVE);
        
        setDescription("""
                       Affected entity bleeds, taking periodic damage.
                       
                       One instance of healing removes bleeding but consumes the healing.
                       """);
    }
    
    @EventHandler
    public void handleGameEntityHealEvent(GameEntityHealEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        
        if (entity.hasEffect(this)) {
            ev.setCancelled(true);
            entity.removeEffect(this);
        }
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        entity.sendMessage("&4∲ &cYou are bleeding!");
        entity.playSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0f);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        entity.sendMessage("&4∲ &aThe bleeding has stopped!");
        entity.playSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        if (effect.tick % damagePeriod == 0) {
            entity.setLastDamager(effect.applier());
            entity.damage(damage, DamageCause.BLEED);
        }
        
        // Always spawn fx
        spawnParticle(entity.getMidpointLocation());
    }
    
    public void spawnParticle(@Nonnull Location location) {
        location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 2, 0.4, 0.2, 0.4, 0, dustTransition);
    }
}
