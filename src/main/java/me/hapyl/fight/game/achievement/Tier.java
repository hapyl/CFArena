package me.hapyl.fight.game.achievement;

import me.hapyl.eterna.module.util.RomanNumber;

import javax.annotation.Nonnull;

public class Tier {

    private final int[] data;
    private final String roman;

    public Tier(int index, int tier, int reward) {
        this.data = new int[] { index, tier, reward };
        this.roman = RomanNumber.toRoman(index + 1);
    }

    public int getIndex() {
        return this.data[0];
    }

    public int getTier() {
        return this.data[1];
    }

    public int getReward() {
        return this.data[2];
    }

    @Nonnull
    public String getRoman() {
        return roman;
    }

    @Override
    public String toString() {
        return "Tier{" +
                "index=" + getIndex() +
                ",tier=" + getTier() +
                ",reward=" + getReward() +
                '}';
    }
}
