package me.hapyl.fight.game.heroes.dylan;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Rebuke extends TickingGameTask {
    
    private final GamePlayer player;
    private final LivingGameEntity entity;
    private final double damage;
    private final int threshold;
    
    Rebuke(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage, int threshold) {
        this.player = player;
        this.entity = entity;
        this.damage = damage;
        this.threshold = threshold;
        
        runTaskTimer(0, 1);
    }
    
    @Override
    public void run(int tick) {
        if (tick > threshold) {
            cancel(Type.EXPIRED);
            
            // Fx if expired
            player.playSound(Sound.ENTITY_GENERIC_BURN, 1.75f);
            player.playSound(Sound.ENTITY_GENERIC_BURN, 0.75f);
            
            return;
        }
        
        // Display rebuke time
        final double rebukeTime = threshold - tick;
        
        player.sendSubtitle("&e%s &6&l\uD83D\uDC49 &e%.1fs".formatted(entity.getName(), rebukeTime / 20d), 0, 2, 1);
        player.playSound(Sound.BLOCK_LAVA_POP, 0.75f + (0.75f * ((float) tick / threshold)));
    }
    
    @OverridingMethodsMustInvokeSuper
    public void cancel(@Nonnull Type type) {
        cancel();
    }
    
    @Nonnull
    public GamePlayer player() {
        return player;
    }
    
    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }
    
    public double damage() {
        return damage;
    }
    
    public enum Type {
        EXPIRED,
        REBUKED
    }
}
