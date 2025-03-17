package me.hapyl.fight.game.damage;

import me.hapyl.fight.terminology.EnumTerm;

/**
 * Represents a flag that {@link DamageCause} can have.
 */
public enum DamageFlag {

    /**
     * Can this {@link DamageCause} crit.
     * <p>
     * This flag is default.
     */
    CAN_CRIT,

    /**
     * Is this {@link DamageCause} a true damage.
     * <p>
     * {@link EnumTerm#TRUE_DAMAGE}
     */
    TRUE_DAMAGE,

    /**
     * Is this {@link DamageCause} an absolute damage.
     * <p>
     * {@link EnumTerm#ABSOLUTE_DAMAGE}
     */
    ABSOLUTE_DAMAGE,

    /**
     * Is this {@link DamageCause} a piercing damage.
     * <p>
     * Piercing damage ignores shields.
     */
    PIERCING_DAMAGE,

    /**
     * Is this {@link DamageCause} ignores damage ticks and does not apply attack cooldown.
     */
    IGNORES_DAMAGE_TICKS_AND_ATTACK_COOLDOWN,

    /**
     * Whether this {@link DamageCause} can kill.
     */
    CAN_KILL,

}
