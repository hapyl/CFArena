package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class VulnerableEffect extends Effect implements Listener {
    
    private final double damageMultiplier = 1.5;
    
    VulnerableEffect(Key key) {
        super(key, "\uD83D\uDCA5", "Vulnerable", Color.DARK_CRIMSON, Type.NEGATIVE);
        
        setDescription("""
                       Affected entities take more damage.
                       """);
    }
    
    @EventHandler()
    public void handleGameDamageEvent(GameDamageEvent.Process ev) {
        final LivingGameEntity entity = ev.getEntity();
        
        if (entity.hasEffect(EffectType.VULNERABLE)) {
            ev.multiplyDamage(damageMultiplier);
        }
    }
    
    @Override
    public void onTick(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        if (effect.tick % 20 == 5) {
            // particle.display(entity.getEyeLocation().add(0, 0.5, 0), entity);
        }
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().playSound(Sound.BLOCK_GLASS_BREAK, 0.75f);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
    }
}
