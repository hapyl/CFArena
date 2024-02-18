package me.hapyl.fight.event;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an instance of the damage.
 */
public class DamageInstance implements Cancellable {

    public static final String DAMAGE_FORMAT = "&b&l%s";
    public static final String DAMAGE_FORMAT_CRIT = "&e&l%s&c✷";

    private final LivingGameEntity entity;
    private final double initialDamage;

    protected EnumDamageCause cause;
    protected double damage;
    protected boolean isCrit;

    private LivingGameEntity damager;
    private boolean cancel;

    public DamageInstance(@Nonnull LivingGameEntity entity, double damage) {
        this.entity = entity;
        this.initialDamage = damage;
        this.damage = damage;
    }

    /**
     * Returns true if the damage is critical.
     *
     * @return true if the damage is critical.
     */
    public boolean isCrit() {
        return isCrit;
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
     * Gets the {@link EnumDamageCause} of this damage.
     *
     * @return the cause of this damage.
     */
    @Nullable
    public EnumDamageCause getCause() {
        return cause;
    }

    /**
     * Gets the {@link EnumDamageCause} of this damage; or default value, if the cause is <code>null</code>.
     *
     * @param def - Default value.
     * @return the cause.
     */
    @Nonnull
    public EnumDamageCause getCauseOr(@Nonnull EnumDamageCause def) {
        return this.cause != null ? this.cause : def;
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

    /**
     * Returns true if this cause is {@link EnumDamageCause#ENTITY_ATTACK} <code>or</code> {@link EnumDamageCause#ENTITY_ATTACK_NON_CRIT}.
     *
     * @return true if this cause is an entity attack.
     */
    public boolean isEntityAttack() {
        return cause == EnumDamageCause.ENTITY_ATTACK || cause == EnumDamageCause.ENTITY_ATTACK_NON_CRIT;
    }

    // Calculate damage
    public void calculateDamage() {
        // True damage is, well, "true" damage.
        if (cause != null && cause.isTrueDamage()) {
            damage = initialDamage;
            return;
        }

        if (damager != null) {
            final EntityAttributes damagerAttributes = damager.getAttributes();

            damage = damagerAttributes.calculateOutgoingDamage(damage);
            isCrit = (cause != null && cause.isCanCrit()) && damagerAttributes.isCritical();

            if (isCrit) {
                damage += damage * damagerAttributes.get(AttributeType.CRIT_DAMAGE);
            }
        }

        damage = Math.max(0.0d, entity.getAttributes().calculateIncomingDamage(damage));
    }

    public double getDamageWithinLimit() {
        return Math.min(damage, entity.getAttributes().getHealth() + 1);
    }

    @Nonnull
    public String getDamageFormatted() {
        String stringDamage;

        if (cause != null) {
            stringDamage = cause.getDamageCause().getDamageFormat().format(this);
        }
        else {
            stringDamage = "%.0f".formatted(damage);
        }

        return isCrit ? DAMAGE_FORMAT_CRIT.formatted(stringDamage) : DAMAGE_FORMAT.formatted(stringDamage);
    }

    /**
     * Returns true if the damage would be lethal if dealt.
     *
     * @return true if the damage is lethal.
     */
    public boolean isDamageLethal() {
        return entity.getHealth() - damage <= 0.0d;
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
