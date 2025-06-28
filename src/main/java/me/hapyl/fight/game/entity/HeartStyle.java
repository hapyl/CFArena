package me.hapyl.fight.game.entity;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

/**
 * Represents a player's heart style.
 */
@ApiStatus.NonExtendable
public abstract class HeartStyle {
    
    protected int tick;
    
    HeartStyle(int tick) {
        this.tick = tick;
    }
    
    abstract void apply(@Nonnull GamePlayer player);
    
    @Nonnull
    public static HeartStyle green(int duration) {
        return new HeartStyle(duration) {
            @Override
            void apply(@Nonnull GamePlayer player) {
                player.addPotionEffect(PotionEffectType.POISON, 0, 5);
            }
        };
    }
    
    @Nonnull
    public static HeartStyle green() {
        return green(Integer.MAX_VALUE);
    }
    
    public static HeartStyle black(int duration) {
        return new HeartStyle(duration) {
            @Override
            void apply(@Nonnull GamePlayer player) {
                player.addPotionEffect(PotionEffectType.WITHER, 0, 5);
            }
        };
    }
    
    @Nonnull
    public static HeartStyle black() {
        return black(Integer.MAX_VALUE);
    }
    
    @Nonnull
    public static HeartStyle white(int duration) {
        return new HeartStyle(duration) {
            @Override
            void apply(@Nonnull GamePlayer player) {
                player.setFreezeTicks(5);
            }
        };
    }
    
    @Nonnull
    public static HeartStyle white() {
        return white(Integer.MAX_VALUE);
    }
    
}
