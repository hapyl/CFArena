package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.HeartStyle;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public class WitherDot extends Dot {
    
    private final double damage = 5;
    
    WitherDot(@Nonnull Key key) {
        super(key, "☠", "Wither", Color.SLATE_GRAY, 20, 20);
    }
    
    @Override
    public void affect(@Nonnull DotInstance instance) {
        instance.entity().damage(damage, instance.applier(), DamageCause.WITHER);
    }
    
    @Override
    public void exhaust(@Nonnull DotInstance instance) {
    }
    
    @Override
    public void onTick(@Nonnull DotInstance instance) {
        // Add wither to display black hearts
        final LivingGameEntity entity = instance.entity();
        
        if (entity instanceof GamePlayer player) {
            player.heartStyle(HeartStyle.black(5));
        }
    }
}
