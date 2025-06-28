package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.HeartStyle;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public class PoisonDot extends Dot {
    
    private final double damage = 5;
    
    PoisonDot(@Nonnull Key key) {
        super(key, "☣", "Poison", Color.NEON_GREEN, 20, 20);
        
        setDescription("""
                       Deals periodic &2poison&7 damage.
                       """);
    }
    
    @Nonnull
    @Override
    public String exhaustDescription() {
        return "";
    }
    
    @Override
    public void affect(@Nonnull DotInstance instance) {
        instance.entity().damageNoKnockback(damage, instance.applier(), DamageCause.POISON);
    }
    
    @Override
    public void exhaust(@Nonnull DotInstance instance) {
    }
    
    @Override
    public void onTick(@Nonnull DotInstance instance) {
        // Add poison to display green hearts
        final LivingGameEntity entity = instance.entity();
        
        if (entity instanceof GamePlayer player) {
            player.heartStyle(HeartStyle.green(5));
        }
    }
}
