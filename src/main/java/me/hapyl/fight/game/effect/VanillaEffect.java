package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.color.Color;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class VanillaEffect extends Effect {
    
    private final PotionEffectType effect;
    
    VanillaEffect(Key key, String prefix, String name, Color color, PotionEffectType effect, Type type) {
        super(key, prefix, name, color, type);
        
        this.effect = effect;
    }
    
    @Override
    public void onStart(@Nonnull ActiveEffect effect) {
        effect.entity().addPotionEffect(new org.bukkit.potion.PotionEffect(this.effect, Constants.INFINITE_DURATION, effect.amplifier(), false, false, false));
    }
    
    @Override
    public void onStop(@Nonnull ActiveEffect effect) {
        effect.entity().removePotionEffect(this.effect);
    }
    
}
