package me.hapyl.fight.game.entity;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Callback;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.strict.StrictPackage;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

// We need access to the entity fields so keep it here
@StrictPackage("me.hapyl.fight.game.entity")
public class Shield implements Ticking {
    
    public static final float DEFAULT_SHIELD_STRENGTH = 1.0f;
    public static final double INFINITE_SHIELD = Integer.MAX_VALUE;
    public static final String SHIELD_FORMAT = "&e&l%.0f &e🛡";
    
    protected final LivingGameEntity entity;
    protected final Builder data;
    
    protected double capacity;
    protected int duration;
    
    public Shield(@Nonnull LivingGameEntity entity, final double maxCapacity, @Nonnull Callback<Builder> callback) {
        this.entity = entity;
        this.data = callback.callbackSelf(new Builder(maxCapacity));
        this.capacity = data.initialCapacity;
        this.duration = data.duration;
    }
    
    public Shield(@Nonnull LivingGameEntity entity, final double maxCapacity) {
        this(entity, maxCapacity, builder -> {});
    }
    
    /**
     * Returns true if this shield can shield form the given cause.
     * <br>
     * By default, shields protect from any damage but {@link DamageCause#FALL}.
     *
     * @param cause - Cause.
     * @return true if this shield can shield from the given cause; false otherwise.
     */
    public boolean canShield(@Nullable DamageCause cause) {
        if (cause == null) {
            return true;
        }
        
        return !cause.hasFlag(DamageFlag.PIERCING_DAMAGE) && cause != DamageCause.FALL;
    }
    
    public void regenerate(double amount) {
        final double regenerateAmount = Math.min(capacity + amount, data.maxCapacity);
        capacity = regenerateAmount;
        
        onRegenerate(regenerateAmount);
        updateShield();
    }
    
    @PreprocessingMethod
    public final double takeDamage0(double damage, @Nonnull DamageInstance instance) {
        takeDamage(damage);
        
        // Only call onHit if the shield is still active after hitting it
        if (capacity > 0.0d) {
            onHit(damage, instance.getDamager());
        }
        
        return capacity;
    }
    
    public void takeDamage(double damage) {
        // Check for infinite shield
        if (capacity >= INFINITE_SHIELD) {
            return;
        }
        
        capacity -= damage;
    }
    
    public double getCapacity() {
        return capacity;
    }
    
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }
    
    /**
     * Called upon shield regenerating a certain capacity.
     *
     * @param amount - Amount of capacity regenerated.
     */
    @EventLike
    public void onRegenerate(double amount) {
    }
    
    /**
     * Called upon shield taking a hit.
     *
     * @param amount  - Amount of damage taken.
     * @param damager - The entity who dealt the damage to the shield. {@code null} if self-damage.
     */
    @EventLike
    public void onHit(double amount, @Nullable LivingGameEntity damager) {
        updateShield();
    }
    
    /**
     * Called upon shield being removed, be it because of a new shield or time limit or being broken.
     *
     * @param cause - The cause of the shield removal.
     */
    @EventLike
    public void onRemove(@Nonnull Cause cause) {
    }
    
    /**
     * Called upon this shield being applied to an entity.
     */
    @EventLike
    public void onCreate() {
    }
    
    public final void setShield(@Nonnull LivingGameEntity entity) {
        entity.setShield(this);
    }
    
    public final void onCreate0() {
        // No idea why it suddenly doesn't work, the only thing I changed was the version
        entity.addPotionEffect(PotionEffectType.ABSORPTION, 4, 10000);
        
        updateShield();
        onCreate();
    }
    
    public final void onBreak0() {
        entity.removePotionEffect(PotionEffectType.ABSORPTION);
        onRemove(Cause.BROKEN);
    }
    
    public final void onRemove0() {
        entity.removePotionEffect(PotionEffectType.ABSORPTION);
        onRemove(Cause.REPLACED);
    }
    
    public void display(double damage, @Nonnull Location location) {
        StringDisplay.ascend(entity.getEyeLocation(), "&e🛡 &6%.0f".formatted(damage), 20);
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        if (duration == Constants.INFINITE_DURATION || duration-- > 0) {
            return;
        }
        
        entity.shield = null; // Can't call setShield() because it calls onRemove
        onRemove(Cause.EXPIRED);
    }
    
    public boolean isInfiniteShield() {
        return this.capacity >= INFINITE_SHIELD;
    }
    
    @Nonnull
    public String getCapacityFormatted() {
        return isInfiniteShield() ? "&e∞ &e🛡" : SHIELD_FORMAT.formatted(this.capacity);
    }
    
    private void updateShield() {
        // Update UI indicator
        if (entity instanceof GamePlayer player) {
            final double absorptionAmount = Math.clamp(20 / data.maxCapacity * capacity, 0, 20);
            
            player.getEntity().setAbsorptionAmount(absorptionAmount);
        }
    }
    
    public enum Cause {
        /**
         * The shield capacity has reached 0.
         */
        BROKEN,
        
        /**
         * The shield duration has reached 0.
         * <p>This is only active if {@link #duration} isn't {@link Constants#INFINITE_DURATION}</p>
         *
         * @see Builder#duration(int)
         * @see Builder#duration(Timed)
         */
        EXPIRED,
        
        /**
         * The shield was replaced with another shield.
         */
        REPLACED,
        
        /**
         * The shield was removed because the entity died.
         */
        DEATH
    }
    
    public static class Builder {
        
        private final double maxCapacity;
        private double initialCapacity;
        private float strength;
        private int duration;
        
        Builder(double maxCapacity) {
            this.maxCapacity = maxCapacity;
            this.initialCapacity = maxCapacity;
            this.strength = DEFAULT_SHIELD_STRENGTH;
            this.duration = Constants.INFINITE_DURATION;
        }
        
        public double initialCapacity() {
            return initialCapacity;
        }
        
        @SelfReturn
        public Builder initialCapacity(double initialCapacity) {
            this.initialCapacity = Math.min(initialCapacity, maxCapacity);
            return this;
        }
        
        public float strength() {
            return strength;
        }
        
        @SelfReturn
        public Builder strength(@Range(from = 0, to = 1) float strength) {
            // I don't think limiting the strength to 1 is necessary to allow extra "bulky" shields
            this.strength = strength;
            return this;
        }
        
        public int duration() {
            return duration;
        }
        
        @SelfReturn
        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }
        
        @SelfReturn
        public Builder duration(@Nonnull Timed timed) {
            return duration(timed.getDuration());
        }
    }
}
