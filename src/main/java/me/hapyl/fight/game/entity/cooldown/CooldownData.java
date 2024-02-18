package me.hapyl.fight.game.entity.cooldown;

import javax.annotation.Nonnull;

public class CooldownData {

    private final Cooldown cooldown;
    private final long startedAt;
    private long duration;

    public CooldownData(@Nonnull Cooldown cooldown, long duration) {
        this.cooldown = cooldown;
        this.startedAt = System.currentTimeMillis();
        this.duration = duration;
    }

    public void increase(long amount) {
        this.duration += amount;
    }

    public void decrease(long amount) {
        this.duration -= amount;
    }

    public long getTimeLeft() {
        return duration - (System.currentTimeMillis() - startedAt);
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startedAt >= duration;
    }

    @Override
    public String toString() {
        return "Cooldown{%s, start=%s, duration=%s, isFinished=%s, timeLeft=%s}".formatted(cooldown, startedAt, duration, isFinished(), getTimeLeft());
    }

}
