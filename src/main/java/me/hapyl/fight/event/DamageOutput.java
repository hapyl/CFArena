package me.hapyl.fight.event;

import me.hapyl.spigotutils.module.annotate.Super;

/**
 * Represents wrapped damage output.
 */
public class DamageOutput {

    /**
     * Static access to cancel damage.
     */
    public static final DamageOutput CANCEL = new DamageOutput(0.0d, true);

    private final boolean cancelDamage;
    private double damage; // not an additional damage, override damage

    /**
     * Creates a new DamageOutput with damage.
     *
     * @param damage - override damage, not additional.
     */
    public DamageOutput(double damage) {
        this(damage, false);
    }

    /**
     * Creates a new DamageOutput with damage and cancel event.
     *
     * @param damage      - override damage, not additional.
     * @param cancelEvent - whether to cancel the event.
     */
    @Super
    public DamageOutput(double damage, boolean cancelEvent) {
        this.damage = damage;
        this.cancelDamage = cancelEvent;
    }

    /**
     * Returns if damage will be canceled.
     *
     * @return if damage is canceled.
     */
    public boolean isCancelDamage() {
        return cancelDamage;
    }

    /**
     * Adds damage to the damage output.
     *
     * @param input  - input to get damage from.
     * @param damage - damage to add.
     */
    public DamageOutput addDamage(DamageInput input, double damage) {
        this.damage = input.getDamage() + damage;
        return this;
    }

    /**
     * Returns final damage that will be inflicted.
     *
     * @return final damage that will be inflicted.
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the damage.
     *
     * @param damage - damage to set.
     */
    public DamageOutput setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    @Override
    public String toString() {
        return "DamageOutput{" + "cancelDamage=" + cancelDamage + ", damage=" + damage + '}';
    }
}
