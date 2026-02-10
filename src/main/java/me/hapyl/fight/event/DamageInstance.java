package me.hapyl.fight.event;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.SnapshotAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.MathBoldFont;
import org.bukkit.event.Cancellable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an instance of the damage.
 */
public class DamageInstance implements Cancellable {
    
    public static final String DAMAGE_FORMAT = "&e&l%s";
    
    public static final String CRIT_CHAR = "&c✷";
    public static final String DAMAGE_FORMAT_CRIT = "&6&l%s" + CRIT_CHAR;
    
    protected final SnapshotAttributes entity;
    
    @Nonnull protected DamageCause cause;
    protected double damage;
    protected boolean isCrit;
    
    protected SnapshotAttributes damager;
    
    private double initialDamage;
    private double critIncrease;
    private boolean cancel;
    private boolean shielded;
    @Nullable private String damageDisplaySuffix;
    
    // TODO @Jul 01, 2025 (xanyjl) -> Adds steps
    
    public DamageInstance(@Nonnull LivingGameEntity entity, double damage) {
        this.entity = entity.getAttributes().snapshot();
        this.initialDamage = damage;
        this.damage = damage;
        this.cause = DamageCause.ENTITY_ATTACK;
    }
    
    /**
     * Returns true if the damage is critical.
     *
     * @return true if the damage is critical.
     */
    public boolean isCrit() {
        return isCrit;
    }
    
    public void setCritical(boolean critical) {
        if (damager == null || critical == isCrit || !cause.hasFlag(DamageFlag.CAN_CRIT)) {
            return;
        }
        
        if (critical) {
            isCrit = true;
            critIncrease = 1 + damager.normalized(AttributeType.CRIT_DAMAGE);
            
            damage *= critIncrease;
        }
        else {
            damage /= critIncrease;
            
            isCrit = false;
            critIncrease = 0.0;
        }
    }
    
    @Nonnull
    public SnapshotAttributes entity() {
        return entity;
    }
    
    @Nullable
    public SnapshotAttributes damager() {
        return damager;
    }
    
    /**
     * Gets the entity who took the damage.
     *
     * @return the entity who took the damage.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity.entity();
    }
    
    /**
     * Gets who damaged the entity.
     *
     * @return the damager.
     */
    @Nullable
    public LivingGameEntity getDamager() {
        return damager != null ? damager.entity() : null;
    }
    
    /**
     * Gets the {@link DamageCause} of this damage.
     *
     * @return the cause of this damage.
     */
    @Nonnull
    public DamageCause getCause() {
        return cause;
    }
    
    /**
     * Sets the {@link DamageCause} of this damage.
     *
     * @param cause - New cause.
     */
    public void setCause(@Nonnull DamageCause cause) {
        this.cause = cause;
    }
    
    /**
     * Gets the initial damage, before <b>any</b> calculations are made.
     *
     * @return the initial damage.
     */
    public double getInitialDamage() {
        return initialDamage;
    }
    
    /**
     * Gets the current damage.
     *
     * @return the damage.
     */
    public double getDamage() {
        return damage;
    }
    
    /**
     * Sets the damage of this instance.
     *
     * <h1>Big Note</h1>
     * <h2>This will completely override all the calculations!</h2>
     * <br>
     * Don't use unless you know what you're doing!
     *
     * @param damage - New damage.
     * @see #multiplyDamage(double)
     * @deprecated Deprecated so monkeys see it's strikethrough must be bad, not gonna use it.
     */
    @Deprecated
    public void overrideDamage(double damage) {
        this.damage = damage;
    }
    
    /**
     * Multiply the current damage by the given multiplier.
     *
     * @param multiplier - Damage multiplier.
     */
    public void multiplyDamage(double multiplier) {
        this.damage *= multiplier;
    }
    
    /**
     * Returns true if the damage was cancelled.
     *
     * @return true if the damage was cancelled.
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    /**
     * Sets the cancellation of the damage.
     * <p>
     * <i>If the damage was already cancelled, nothing will happen.</i>
     *
     * @param cancel true if you wish to cancel the damage.
     */
    @Override
    public void setCancelled(boolean cancel) {
        // Don't allow to "uncancel" the event
        if (this.cancel) {
            return;
        }
        
        this.cancel = cancel;
    }
    
    /**
     * Gets the damager as a {@link GamePlayer}.
     *
     * @return the damager as a player; or null if there is no damager, or it's not a player.
     */
    @Nullable
    public GamePlayer getDamagerAsPlayer() {
        final LivingGameEntity damager = getDamager();
        
        if (damager instanceof GamePlayer player) {
            return player;
        }
        
        return null;
    }
    
    /**
     * Gets the entity as a {@link GamePlayer}.
     *
     * @return the damager as a player; or throws an error.
     * @throws IllegalArgumentException if the entity is <code>not</code> a {@link GamePlayer}.
     */
    @Nonnull
    public GamePlayer getEntityAsPlayer() throws IllegalArgumentException {
        final LivingGameEntity entity = getEntity();
        
        if (entity instanceof GamePlayer player) {
            return player;
        }
        
        throw new IllegalArgumentException("Entity is not a player!");
    }
    
    public boolean isDirectDamage() {
        return cause.isDirectDamage();
    }
    
    // Calculate damage
    public void calculateDamage() {
        // Absolute damage ignores EVERYTHING
        if (cause.hasFlag(DamageFlag.TRUE_DAMAGE)) {
            damage = initialDamage;
            return;
        }
        
        if (damager != null) {
            damage = damager.calculate().outgoingDamage(initialDamage, cause);
            
            // Calculate crit
            setCritical(damager.calculate().critical());
        }
        
        damage = Math.max(0.0d, entity.calculate().incomingDamage(damage, cause));
    }
    
    public double getDamageWithinLimit() {
        return Math.min(damage, entity.getMaxHealth() + 1);
    }
    
    @Nonnull
    public String getDamageFormatted() {
        final String damageString = MathBoldFont.format("%.0f".formatted(damage));
        final String formatted = isCrit ? DAMAGE_FORMAT_CRIT.formatted(damageString) : DAMAGE_FORMAT.formatted(damageString);
        
        return damageDisplaySuffix != null ? formatted + " " + damageDisplaySuffix : formatted;
    }
    
    public boolean shielded() {
        return shielded;
    }
    
    public void markShielded() {
        this.shielded = true;
    }
    
    @Nullable
    public String damageDisplaySuffix() {
        return damageDisplaySuffix;
    }
    
    public void damageDisplaySuffix(@Nullable String damageDisplaySuffix) {
        this.damageDisplaySuffix = damageDisplaySuffix;
    }
    
    public void overrideInitialDamage(double newDamage) {
        this.initialDamage = newDamage;
        this.damage = newDamage;
    }
    
    protected void setLastDamager(@Nullable LivingGameEntity entity) {
        // Nullate
        if (entity == null) {
            damager = null;
            return;
        }
        
        // Reassign
        damager = entity.getAttributes().snapshot();
    }
}
