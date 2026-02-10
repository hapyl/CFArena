package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class FallDamageResistanceEffect extends Effect implements Listener {
    
    FallDamageResistanceEffect(Key key) {
        super(key, "\uD83E\uDDB6", "Fall Damage Resistance", Color.LAVENDER_GRAY, Type.POSITIVE);
        
        setDescription("""
                       Negates the first instance of fall damage.
                       """);
    }
    
    @EventHandler()
    public void handleDamageEvent(GameDamageEvent.Process ev) {
        final LivingGameEntity entity = ev.getEntity();
        final DamageCause cause = ev.getCause();
        
        if (cause != DamageCause.FALL || !entity.hasEffect(this)) {
            return;
        }
        
        ev.setCancelled(true);
        entity.removeEffect(EffectType.FALL_DAMAGE_RESISTANCE);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
    }
}
