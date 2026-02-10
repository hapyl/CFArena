package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.stats.StatType;

public class StatValue implements Rankable {

    public final Hero hero;
    public final StatType type;
    public final double value;
    private int rank;

    public StatValue(Hero hero, StatType type, double value) {
        this.hero = hero;
        this.type = type;
        this.value = value;
        this.rank = -1;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "{%s, %s=%.1f}".formatted(hero, type, value);
    }
}
