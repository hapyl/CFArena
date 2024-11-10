package me.hapyl.fight.game.entity;

import me.hapyl.eterna.module.util.Ticking;

public class Decay implements Ticking {

    private final double amount;
    private final int duration;
    private final double decrement;

    private double decay;

    public Decay(double amount, int duration) {
        this.amount = amount;
        this.duration = duration;

        this.decay = amount;
        this.decrement = amount / duration;
    }

    public double getAmount() {
        return this.amount;
    }

    public int getDuration() {
        return this.duration;
    }

    public double getDecay() {
        return this.decay;
    }

    @Override
    public void tick() {
        this.decay -= this.decrement;
    }
}
