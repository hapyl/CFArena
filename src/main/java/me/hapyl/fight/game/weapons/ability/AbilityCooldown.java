package me.hapyl.fight.game.weapons.ability;

public class AbilityCooldown {

    private final long startedAt;
    private final long duration;

    public AbilityCooldown(long startedAt, long duration) {
        this.startedAt = startedAt;
        this.duration = duration;
    }

    public boolean isOver() {
        return System.currentTimeMillis() - startedAt >= duration;
    }

    public int getTimeLeft() {
        return (int) (getTimeLeftInMillis() / 50);
    }

    public long getTimeLeftInMillis() {
        return duration - (System.currentTimeMillis() - startedAt);
    }
}
