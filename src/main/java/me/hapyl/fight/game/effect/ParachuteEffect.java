package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ParachuteEffect extends Effect {
    ParachuteEffect(Key key) {
        super(key, "\uD83E\uDE82", "Parachute", Color.STONE_GRAY, Type.POSITIVE);
        
        setDescription("""
                       Affected entity descends slowly until grounded.
                       """);
    }
    
    @Override
    public boolean shouldRemove(@Nonnull ActiveEffect effect) {
        return LingerEffect.isOnGround(effect);
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().addPotionEffect(PotionEffectType.SLOW_FALLING, 0, PotionEffect.INFINITE_DURATION);
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        effect.entity().removePotionEffect(PotionEffectType.SLOW_FALLING);
    }
}
