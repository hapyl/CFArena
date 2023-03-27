package me.hapyl.fight.event;

import me.hapyl.spigotutils.module.annotate.Super;

public class DamageOutput {

    public static final DamageOutput CANCEL = new DamageOutput(0.0d, true);

    private final boolean cancelDamage;
    private double damage; // not an additional damage, override damage

    public DamageOutput(double damage) {
        this(damage, false);
    }

    @Super
    public DamageOutput(double damage, boolean cancelEvent) {
        this.damage = damage;
        this.cancelDamage = cancelEvent;
    }

    public boolean isCancelDamage() {
        return cancelDamage;
    }

    public DamageOutput addDamage(DamageInput input, double damage) {
        this.damage = input.getDamage() + damage;
        return this;
    }

    public DamageOutput setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public double getDamage() {
        return damage;
    }
}
