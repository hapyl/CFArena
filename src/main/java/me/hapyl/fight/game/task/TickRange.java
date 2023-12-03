package me.hapyl.fight.game.task;

import java.util.Objects;

public class TickRange {

    public final int min;
    public final int max;

    public TickRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean isWithinRange(int value) {
        return value >= min && value <= max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TickRange tickRange = (TickRange) o;
        return min == tickRange.min && max == tickRange.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public String toString() {
        return min + "-" + max;
    }
}
