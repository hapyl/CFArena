package me.hapyl.fight.game;

import com.google.common.collect.Lists;

import java.util.List;

public class DamageCause {

    public static final DamageCause EMPTY = new DamageCause("null", "");

    private final List<DeathMessage> deathMessages;
    private DamageFormat damageFormat;

    private boolean custom;
    private boolean canCrit;

    private DamageCause() {
        this.deathMessages = Lists.newArrayList();
        this.canCrit = true;
        this.custom = true;
    }

    private DamageCause(String string, String suffix) {
        this();
        deathMessages.add(new DeathMessage(string, suffix));
    }

    public List<DeathMessage> getDeathMessages() {
        return deathMessages;
    }

    public DamageFormat getDamageFormat() {
        return damageFormat;
    }

    public boolean isCustom() {
        return custom;
    }

    public DamageCause setCustom(boolean custom) {
        this.custom = custom;
        return this;
    }

    public boolean isCanCrit() {
        return canCrit;
    }

    public DamageCause setCanCrit(boolean canCrit) {
        this.canCrit = canCrit;
        return this;
    }

    @Override
    protected DamageCause clone() {
        final DamageCause clone = new DamageCause();

        clone.custom = custom;
        clone.canCrit = canCrit;
        clone.deathMessages.addAll(Lists.newArrayList(deathMessages));
        clone.damageFormat = damageFormat;

        return clone;
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
        return of(message, "");
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
