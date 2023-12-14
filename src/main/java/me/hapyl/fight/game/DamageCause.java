package me.hapyl.fight.game;

import javax.annotation.Nonnull;

public class DamageCause implements Cloneable {

    public static final DamageCause EMPTY = new DamageCause("null", "null");

    private final DeathMessage deathMessage;
    public boolean isProjectile;
    protected boolean custom;
    protected boolean canCrit;
    protected boolean isTrueDamage;
    private DamageFormat damageFormat;

    private DamageCause(@Nonnull DeathMessage message) {
        this.deathMessage = message;
        this.canCrit = true;
        this.custom = true;
        this.isProjectile = false;
        this.isTrueDamage = false;
        this.damageFormat = DamageFormat.DEFAULT;
    }

    private DamageCause(String string, String suffix) {
        this(new DeathMessage(string, suffix));
    }

    @Nonnull
    public DeathMessage getDeathMessage() {
        return deathMessage;
    }

    @Nonnull
    public DamageFormat getDamageFormat() {
        return damageFormat;
    }

    public DamageCause setDamageFormat(DamageFormat damageFormat) {
        this.damageFormat = damageFormat;
        return this;
    }

    public DamageCause setTrueDamage() {
        isTrueDamage = true;
        return this;
    }

    public boolean isCustom() {
        return custom;
    }

    public DamageCause setCustom(boolean custom) {
        this.custom = custom;
        return this;
    }

    public boolean isTrueDamage() {
        return isTrueDamage;
    }

    public boolean isCanCrit() {
        return canCrit;
    }

    public DamageCause setCanCrit(boolean canCrit) {
        this.canCrit = canCrit;
        return this;
    }

    public DamageCause setProjectile(boolean projectile) {
        this.isProjectile = projectile;
        return this;
    }

    @Override
    @Nonnull
    public DamageCause clone() {
        try {
            final DamageCause clone = (DamageCause) super.clone();

            clone.custom = custom;
            clone.canCrit = canCrit;
            clone.damageFormat = damageFormat;

            return clone;
        } catch (CloneNotSupportedException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Creates a DamageCause that cannot crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    public static DamageCause nonCrit(String message, String suffix) {
        return of(message, suffix).setCanCrit(false);
    }

    /**
     * Creates a DamageCause that cannot crit.
     *
     * @param message - Message.
     */
    public static DamageCause nonCrit(String message) {
        return nonCrit(message, "");
    }

    /**
     * Creates a DamageCause that can crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    public static DamageCause of(String message, String suffix) {
        return new DamageCause(message, suffix);
    }

    /**
     * Creates a DamageCause that can crit.
     *
     * @param message - Message.
     */
    public static DamageCause of(String message) {
        return of(message, "by");
    }

    /**
     * Creates a DamageCause that are minecraft vanilla and cannot crit.
     *
     * @param message - Message.
     * @param suffix  - Suffix.
     */
    public static DamageCause minecraft(String message, String suffix) {
        return new DamageCause(message, suffix).setCustom(false).setCanCrit(false);
    }

    /**
     * Creates a DamageCause that are minecraft vanilla and cannot crit.
     *
     * @param message - Message.
     */
    public static DamageCause minecraft(String message) {
        return minecraft(message, "");
    }
}
