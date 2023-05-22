package me.hapyl.fight.game;

import com.google.common.collect.Lists;

import java.util.List;

public class DamageCause {

    public static final DamageCause EMPTY = new DamageCause("null", "");

    private final List<DeathMessage> deathMessages;
    private DamageFormat damageFormat;

    private boolean custom;
    private boolean canCrit;

    public DamageCause() {
        this.deathMessages = Lists.newArrayList();
        this.canCrit = true;
        this.custom = true;
    }

    public DamageCause(String string, String suffix) {
        this();
        deathMessages.add(new DeathMessage(string, suffix));
    }

    public DamageCause setCustom(boolean custom) {
        this.custom = custom;
        return this;
    }

    public DamageCause setCanCrit(boolean canCrit) {
        this.canCrit = canCrit;
        return this;
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

    public boolean isCanCrit() {
        return canCrit;
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

    public static DamageCause of(String message, String suffix) {
        return new DamageCause(message, suffix);
    }

    public static DamageCause of(String message) {
        return of(message, "");
    }

    public static DamageCause minecraft(String message, String suffix) {
        return new DamageCause(message, suffix).setCustom(false);
    }

    public static DamageCause minecraft(String message) {
        return minecraft(message, "");
    }
}
