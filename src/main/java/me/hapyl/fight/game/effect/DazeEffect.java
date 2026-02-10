package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class DazeEffect extends Effect implements Listener {
    
    private final double missChanceBase = 0.3;
    private final double missChancePerLevel = 0.15;
    
    DazeEffect(@Nonnull Key key) {
        super(key, "\uD83D\uDE35", "Daze", Color.MUTED_PURPLE, Type.NEGATIVE);
        
        setDescription("""
                       Dazed entities have a chance for their attack to miss.
                       &8&o;;missChance = %.1f + (%.1f * amplifier)
                       """.formatted(missChanceBase, missChancePerLevel));
    }
    
    @EventHandler
    public void handleDamageEvent(GameDamageEvent.Process ev) {
        if (!(ev.getDamager() instanceof LivingGameEntity damager)) {
            return;
        }
        
        final ActiveEffect effect = damager.getActiveEffect(this);
        
        if (effect == null) {
            return;
        }
        
        final double missChance = missChanceBase + (missChancePerLevel * effect.amplifier());
        
        // Check for miss
        if (!damager.random.checkBound(missChance)) {
            return;
        }
        
        ev.setCancelled(true);
        
        final LivingGameEntity entity = ev.getEntity();
        
        entity.spawnBuffDisplay("&c&lᴍɪꜱꜱ&4!!!", 25);
        entity.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 0.75f);
        entity.playWorldSound(Sound.ENTITY_WARDEN_TENDRIL_CLICKS, 1.75f);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        final Location location = entity.getEyeLocation();
        
        // Display stars above the head
        final double rad = Math.toRadians(effect.tick * 5);
        final double offset = Math.PI * 2 / 3;
        
        for (double d = 0; d < 3; d++) {
            final double x = Math.sin(rad + offset * d) * 0.7;
            final double y = Math.sin(rad * 5) * 0.1 + 0.3;
            final double z = Math.cos(rad + offset * d) * 0.7;
            
            LocationHelper.offset(
                    location, x, y, z, () -> effect.particle(location, Particle.WITCH, 1)
            );
        }
    }
}
