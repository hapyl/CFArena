package me.hapyl.fight.event;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.WeakEntityAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Disposable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an instance of the damage.
 */
public class DamageInstance implements Cancellable, Disposable {

    public static final String DAMAGE_FORMAT = "&b&l%s";
    public static final String DAMAGE_FORMAT_CRIT = "&e&l%s&c✷";

    private final InstanceEntityData entity;
    private final double initialDamage;

    protected EnumDamageCause cause;
    protected double damage;
    protected boolean isCrit;

    private double critAt = 0.0d;
    private InstanceEntityData damager;
    private boolean cancel;
    private double multiplier;

    public DamageInstance(@Nonnull LivingGameEntity entity, double damage) {
        this.entity = new InstanceEntityData(entity);
        this.initialDamage = damage;
        this.damage = damage;
        this.multiplier = 1.0d;
    }

    /**
     * Returns true if the damage is critical.
     * <p>
     * <b>If called inside {@link me.hapyl.fight.event.custom.GameDamageEvent}, this value is always <code>false</code>, since the calculations are done after the event.</b>
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
        return entity.entity;
    }

    /**
     * Gets the data of the entity who took the damage.
     *
     * @return entity's data.
     * @see InstanceEntityData
     */
    @Nonnull
    public InstanceEntityData getEntityData() {
        return entity;
    }

    /**
     * Gets who damaged the entity.
     *
     * @return the damager.
     */
    @Nullable
    public LivingGameEntity getDamager() {
        return damager != null ? damager.entity : null;
    }

    /**
     * Gets the data of who damaged the entity.
     *
     * @return damager's data.
     * @see InstanceEntityData
     */
    @Nullable
    public InstanceEntityData getDamagerData() {
        return damager;
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
     * <h1>Big Note!</h1>
     * Setting the damage in
     * {@link me.hapyl.fight.event.custom.GameDamageEvent},
     * {@link me.hapyl.fight.game.heroes.Hero#processDamageAsDamager(DamageInstance)},
     * {@link me.hapyl.fight.game.heroes.Hero#processDamageAsVictim(DamageInstance)}}
     * is <b>not</b> supported.
     * <p>
     * Don't use unless you KNOW what's your doing, see {@link #setDamageMultiplier(double)}.
     *
     * @param damage - New damage.
     * @see #setDamageMultiplier(double)
     */
    public void setInternalDamage(double damage) {
        this.damage = damage;
    }

    public double getDamageMultiplier() {
        return multiplier;
    }

    /**
     * Sets the final multiplier for this damage.
     *
     * @param multiplier - Damage multiplier.
     */
    public void setDamageMultiplier(double multiplier) {
        this.multiplier = Math.max(multiplier, 0.0d);
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

    /**
     * Disposes of the instance.
     * <p>
     * Should <b>only</b> be called at the end of the {@link PlayerHandler#handleDamage0(EntityDamageEvent)}.
     */
    @Override
    public void dispose() {
        entity.dispose();

        if (damager != null) {
            damager.dispose();
        }
    }

    // Calculate damage
    public void calculateDamage() {
        // True damage is, well, "true" damage.
        if (cause != null && cause.isTrueDamage()) {
            damage = initialDamage;
            return;
        }

        if (damager != null) {
            final WeakEntityAttributes damagerAttributes = damager.getAttributes();

            damage = damagerAttributes.calculateOutgoingDamage(damage);
            isCrit = (cause != null && cause.isCanCrit()) && damagerAttributes.isCritical();

            if (isCrit) {
                critAt = damagerAttributes.get(AttributeType.CRIT_CHANCE);
                damage += damage * damagerAttributes.get(AttributeType.CRIT_DAMAGE);
            }
        }

        damage = Math.max(0.0d, entity.getAttributes().calculateIncomingDamage(damage));
        damage *= multiplier;
    }

    // Recalculate damage again in case attributes changed
    public void recalculateDamage() {
        // True damage is, well, "true" damage.
        if (cause != null && cause.isTrueDamage()) {
            damage = initialDamage;
            return;
        }

        // Default damage
        damage = initialDamage;

        if (damager != null) {
            final WeakEntityAttributes damagerAttributes = damager.getAttributes();

            damage = damagerAttributes.calculateOutgoingDamage(damage);

            // If was crit before, check if still have enough crit chance
            if (isCrit) {
                final double critChance = damagerAttributes.get(AttributeType.CRIT_CHANCE);

                // If >= to initial crit chance, recalculate with new crit damage
                if (critChance >= critAt) {
                    damage += damage * damagerAttributes.get(AttributeType.CRIT_DAMAGE);
                }
                // Else remove crit
                else {
                    isCrit = false;
                }
            }
        }

        damage = Math.max(0.0d, entity.getAttributes().calculateIncomingDamage(damage));
        damage *= multiplier;
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

    protected void setLastDamager(@Nullable LivingGameEntity entity) {
        // Dispose of the old damager
        if (damager != null) {
            damager.dispose();
        }

        // Nullate
        if (entity == null) {
            damager = null;
            return;
        }

        // Reassign
        damager = new InstanceEntityData(entity);
    }
}
