package me.hapyl.fight.event.custom;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.SnapshotAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a collection of game-related damage events for a given state, which includes:
 * <ul>
 *     <li>{@link PreProcess}
 *     <p>Called before any damage calculations are made, allowing modifying attributes before they're used in calculations.
 *     <p>This state allows changing the initial damage of the instance, see {@link PreProcess#initialDamage(double)}.
 * <p>
 *
 *     <li>{@link Process}
 *     <p>Called after damage calculations are made, but before any hero-related methods are called, allowing cancelling or modifying the event before hero-mechanics are executed.
 *     Note that cancelling the event will stop any hero-related mechanics from working.
 * <p>
 *
 *     <li>{@link PostProcess}
 *     <p>Called after damage calculations and hero-methods are called, mainly used for "monitor" purposes.
 * </ul>
 */
@ApiStatus.NonExtendable
public abstract class GameDamageEvent extends CustomEvent {
    
    protected final DamageInstance instance;
    
    GameDamageEvent(@Nonnull DamageInstance instance) {
        this.instance = instance;
    }
    
    /**
     * Gets the entity attributes.
     *
     * @return the entity attributes.
     */
    @Nonnull
    public SnapshotAttributes entity() {
        return instance.entity();
    }
    
    /**
     * Gets the entity.
     *
     * @return the entity.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return instance.getEntity();
    }
    
    /**
     * Gets the damager attributes.
     *
     * @return the damager attributes.
     */
    @Nullable
    public SnapshotAttributes damager() {
        return instance.damager();
    }
    
    /**
     * Gets the damager.
     *
     * @return the damager.
     */
    @Nullable
    public GameEntity getDamager() {
        return instance.getDamager();
    }
    
    /**
     * Gets the damage of the instance.
     * <p>
     * Note that if called on {@link PreProcess}, the damage is the initial damage, before calculations.</p>
     *
     * @return the damage of the instance.
     */
    public double getDamage() {
        return instance.getDamage();
    }
    
    /**
     * Gets the cause of the instance.
     *
     * @return the cause of the instance.
     */
    @Nonnull
    public DamageCause getCause() {
        return instance.getCause();
    }
    
    /**
     * Gets whether the damage was critical.
     * <p>Note that if called on {@link PreProcess}, the value if always {@code false}.</p>
     *
     * @return whether the damage was critical.
     */
    public boolean isCrit() {
        return instance.isCrit();
    }
    
    /**
     * Gets whether the damage was shielded.
     * <p>Note that if called on {@link PreProcess}, the value if always {@code false}.</p>
     *
     * @return whether the damage was shielded.
     */
    public boolean shielded() {
        return instance.shielded();
    }
    
    public static boolean callPreProcessEvent(@Nonnull DamageInstance instance) {
        return new PreProcess(instance).callEvent();
    }
    
    public static boolean callProcessEvent(@Nonnull DamageInstance instance) {
        return new Process(instance).callEvent();
    }
    
    public static void callPostProcessEvent(@Nonnull DamageInstance instance) {
        new PostProcess(instance).callEvent();
    }
    
    /**
     * A pre-processing damage vent.
     *
     * <p>Called before any calculations are made, allowing changing {@link SnapshotAttributes}, cancelling the event and overriding initial damage.
     *
     * @see #entity()
     * @see #damager()
     * @see #initialDamage(double)
     * @see #setCancelled(boolean)
     */
    public static class PreProcess extends GameDamageEvent implements Cancellable {
        
        private static final HandlerList handlers = new HandlerList();
        
        private boolean cancel;
        
        PreProcess(@Nonnull DamageInstance instance) {
            super(instance);
        }
        
        /**
         * Gets the initial damage of the instance.
         *
         * @return the initial damage of the instance.
         */
        public double initialDamage() {
            return instance.getInitialDamage();
        }
        
        /**
         * Sets the initial damage of the instance.
         * <p>Keep in mind the initial damage is the damage that all the calculations will be based on.</p>
         *
         * @param initialDamage - The new initial damage.
         */
        public void initialDamage(double initialDamage) {
            instance.overrideInitialDamage(initialDamage);
        }
        
        @Override
        @Nonnull
        public HandlerList getHandlers() {
            return handlers;
        }
        
        @Override
        public boolean isCancelled() {
            return cancel;
        }
        
        @Override
        public void setCancelled(boolean cancel) {
            this.cancel = cancel;
        }
        
        @Nonnull
        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
    
    /**
     * A processing event.
     *
     * <p>Called after damage calculations are made but before any hero-related methods are called.
     * Allows cancelling the event or changing the damage.
     *
     * @see #setCancelled(boolean)
     * @see #multiplyDamage(double)
     */
    public static class Process extends GameDamageEvent implements Cancellable {
        
        private static final HandlerList handlerList = new HandlerList();
        
        private boolean cancel;
        
        Process(@Nonnull DamageInstance instance) {
            super(instance);
        }
        
        /**
         * Multiplies the damage by the given multiplier.
         *
         * @param multiplier - The multiplier to multiply by.
         */
        public void multiplyDamage(double multiplier) {
            instance.multiplyDamage(multiplier);
        }
        
        /**
         * Sets the damage display suffix of the instance.
         *
         * @param suffix - The new suffix.
         */
        public void damageDisplaySuffix(@Nullable String suffix) {
            instance.damageDisplaySuffix(suffix);
        }
        
        @Override
        public boolean isCancelled() {
            return cancel;
        }
        
        @Override
        public void setCancelled(boolean cancel) {
            this.cancel = cancel;
        }
        
        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlerList;
        }
        
        @Nonnull
        public static HandlerList getHandlerList() {
            return handlerList;
        }
    }
    
    /**
     * A post-processing event.
     *
     * <p>Called after every calculation is made and any hero-related methods are called.
     * Allows monitoring the damage, modifying {@link SnapshotAttributes} does absolutely nothing.
     */
    public static class PostProcess extends GameDamageEvent {
        private static final HandlerList handlerList = new HandlerList();
        
        PostProcess(@Nonnull DamageInstance instance) {
            super(instance);
        }
        
        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlerList;
        }
        
        @Nonnull
        public static HandlerList getHandlerList() {
            return handlerList;
        }
    }
}
