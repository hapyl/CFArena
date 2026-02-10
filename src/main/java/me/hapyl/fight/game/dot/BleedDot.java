package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class BleedDot extends Dot implements Listener {
    
    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            org.bukkit.Color.fromRGB(125, 1, 20),
            org.bukkit.Color.fromRGB(194, 14, 41),
            2
    );
    
    private final double damageOfMaxHealth = 0.01; // % of max health
    
    BleedDot(@Nonnull Key key) {
        super(key, "∲", "Bleeding", Color.BLOOD_RED, 15, 50);
        
        setDescription("""
                       Affected entity &4bleeds&7, taking periodic &cdamage&7.
                       
                       One instance of &ahealing&7 removes bleeding but consumes the healing.
                       """);
    }
    
    @EventHandler
    public void handleGameEntityHealEvent(GameEntityHealEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        
        if (entity.hasDot(this)) {
            ev.setCancelled(true);
            entity.removeDot(this);
        }
    }
    
    @Override
    public void affect(@Nonnull DotInstance instance) {
        final LivingGameEntity entity = instance.entity();
        final double damage = damageOfMaxHealth * entity.getMaxHealth();
        
        final LivingGameEntity applier = instance.applier();
        
        if (applier != null) {
            entity.setLastDamager(applier);
        }
        
        entity.damage(damage, DamageCause.BLEED);
    }
    
    @Override
    public void onStart(@Nonnull DotInstance instance) {
        final LivingGameEntity entity = instance.entity();
        
        entity.sendMessage("&4∲ &cYou are bleeding!");
        entity.playSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0f);
    }
    
    @Override
    public void onStop(@Nonnull DotInstance instance) {
        final LivingGameEntity entity = instance.entity();
        
        entity.sendMessage("&4∲ &aThe bleeding has stopped!");
        entity.playSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
    }
    
    @Override
    public void onTick(@Nonnull DotInstance instance) {
        spawnParticle(instance.entity().getMidpointLocation());
    }
    
    @Override
    public void exhaust(@Nonnull DotInstance instance) {
    }
    
    public void spawnParticle(@Nonnull Location location) {
        location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 1, 0.4, 0.2, 0.4, 0, dustTransition);
    }
}
