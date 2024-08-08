package me.hapyl.fight.game.dot;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Described;
import me.hapyl.eterna.module.math.Numbers;

import javax.annotation.Nonnull;

public abstract class Dot implements Described {

    private final String name;
    private final String description;

    private int period;
    private int maxStacks;
    private double damage;
    @Nonnull private EnumDamageCause cause;

    public Dot(String name, String description) {
        this.name = name;
        this.description = description;
        this.period = 20;
        this.maxStacks = 3;
        this.damage = 0.0d;
        this.cause = EnumDamageCause.ENTITY_ATTACK;
    }

    public abstract void affect(@Nonnull LivingGameEntity entity);

    public void setCause(@Nonnull EnumDamageCause cause) {
        this.cause = cause;
    }

    @Nonnull
    public EnumDamageCause getCause() {
        return cause;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = Math.max(damage, 0);
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public void setMaxStacks(int maxStacks) {
        this.maxStacks = Numbers.clamp(maxStacks, 1, 20);
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = Numbers.clamp(period, 1, 100);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
}
