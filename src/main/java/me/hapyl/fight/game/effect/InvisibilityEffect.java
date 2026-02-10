package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Collect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class InvisibilityEffect extends Effect {
    
    InvisibilityEffect(Key key) {
        super(key, "\uD83D\uDC7B", "Invisibility", Color.ICE_BLUE, Type.POSITIVE);
        
        setDescription("""
                       Completely hides the entity.
                       """);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        entity.addPotionEffectIndefinitely(PotionEffectType.INVISIBILITY, 1);
        
        if (entity instanceof GamePlayer player) {
            player.hidePlayer();
            
            // Lose mob aggro is not valid
            if (!player.getHero().isValidIfInvisible(player)) {
                Collect.nearbyEntities(entity.getLocation(), 25).forEach(target -> {
                    final LivingGameEntity targetEntity = target.getTargetEntity();
                    
                    if (targetEntity != entity) {
                        return;
                    }
                    
                    target.setTarget(null);
                });
            }
        }
        
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        final LivingGameEntity entity = effect.entity();
        
        if (entity instanceof GamePlayer player) {
            player.showPlayer();
        }
        
        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}
