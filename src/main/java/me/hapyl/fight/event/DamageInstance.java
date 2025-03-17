package me.hapyl.fight.event;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
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

    private final LivingGameEntity entity;

    @Nonnull protected DamageCause cause;
    protected double damage;
    protected boolean isCrit;

    private LivingGameEntity damager;
    private boolean cancel;
    private double initialDamage;

    public DamageInstance(@Nonnull LivingGameEntity entity, double damage) {
        this.entity = entity;
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

    public void setCrit() {
        // Don't care if already scored a crit or environment damage
        if (damager == null || isCrit) {
            return;
        }

        final EntityAttributes attributes = damager.getAttributes();

        isCrit = true;
        damage = damage * (1 + attributes.get(AttributeType.CRIT_DAMAGE));
    }

    /**
     * Gets the entity who took the damage.
     *
     * @return the entity who took the damage.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    /**
     * Gets who damaged the entity.
     *
     * @return the damager.
     */
    @Nullable
    public LivingGameEntity getDamager() {
        return damager != null ? damager : null;
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
    public void setDamage(double damage) {
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
        if (cause.hasFlag(DamageFlag.ABSOLUTE_DAMAGE)) {
            damage = initialDamage;
            return;
        }

        if (damager != null) {
            final EntityAttributes damagerAttributes = damager.getAttributes();

            damage = damagerAttributes.calculateOutgoingDamage(initialDamage, cause);

            // Calculate crit
            final boolean shouldCrit = cause.hasFlag(DamageFlag.CAN_CRIT) && damagerAttributes.isCritical();

            if (shouldCrit) {
                setCrit();
            }
        }

        damage = Math.max(0.0d, entity.getAttributes().calculateIncomingDamage(damage, damager, cause));
    }

    public double getDamageWithinLimit() {
        return Math.min(damage, entity.getAttributes().getMaxHealth() + 1);
    }

    @Nonnull
    public String getDamageFormatted() {
        final String damageString = MathBoldFont.format("%.0f".formatted(damage));

        return isCrit
                ? DAMAGE_FORMAT_CRIT.formatted(damageString)
                : DAMAGE_FORMAT.formatted(damageString);
    }

    /**
     * Returns true if the damage would be lethal if dealt.
     *
     * @return true if the damage is lethal.
     */
    public boolean isDamageLethal() {
        return entity.getHealth() - damage <= 0.0d;
    }

    protected void overrideInitialDamage(double newDamage) {
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
        damager = entity;
    }
}
