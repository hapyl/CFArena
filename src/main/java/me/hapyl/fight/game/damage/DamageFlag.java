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
    PIERCING_DAMAGE,

    /**
     * Is this {@link DamageCause} ignores damage ticks and does not apply attack cooldown.
     */
    IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN,

    /**
     * Is this {@link DamageCause} a environment damage.
     * <br>
     * Environment damage
     */
    ENVIRONMENT,

    /**
     * Whether this {@link DamageCause} can kill.
     */
    CAN_KILL,

    /**
     * Whether this {@link DamageCause} should be considered a 'melee' hit.
     */
    MELEE,

}
