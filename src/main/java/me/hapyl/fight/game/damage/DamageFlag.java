package me.hapyl.fight.game.damage;

/**
 * Represents a flag that {@link DamageCause} can have.
 */
public enum DamageFlag {

    /**
     * Is this {@link DamageCause} supposed to be a projectile.
     * <p>
     * Projectile kills display the distance of the shot.
     */
    PROJECTILE,
    /**
     * Is this {@link DamageCause} is custom damage or vanilla damage.
     * <p>
     * This flag is default for custom causes.
     */
    CUSTOM,
    /**
     * Can this {@link DamageCause} crit.
     * <p>
     * This flag is default.
     */
    CAN_CRIT,
    /**
     * Is this {@link DamageCause} a true damage.
     * <p>
     * True damage is not affected by attributes.
     */
    TRUE_DAMAGE,
    /**
     * Is this {@link DamageCause} a piercing damage.
     * <p>
     * Piercing damage ignores shields.
     */
    PIERCING_DAMAGE;

}
