package me.hapyl.fight.game.maps.features;

import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.util.Vector;

public enum Direction {

    POSITIVE_X(1, 0, 0),
    NEGATIVE_X(-1, 0, 0),

    POSITIVE_Y(0, 1, 0),
    NEGATIVE_Y(0, -1, 0),

    POSITIVE_Z(0, 0, 1),
    NEGATIVE_Z(0, 0, -1),

    NONE(0, 0, 0);

    private final int[] values;

    Direction(int x, int y, int z) {
        this.values = new int[] { x, y, z };
    }

    public Vector createVector() {
        return new Vector(getMagnitude(0), getMagnitude(1), getMagnitude(2));
    }

    public double getMagnitude(int i) {
        i = Numbers.clamp(i, 0, values.length - 1);

        final int value = values[i];
        return value == 0 ? 0.0d : Turbine.MAGNITUDE * value;
    }

    public double getValue(int index, double value, double def) {
        final int val = values[index];

        if (val == 0) {
            return def;
        }

        return value;
    }

    public int[] getValues() {
        return values;
    }

    public boolean isAxisX() {
        return this == POSITIVE_X || this == NEGATIVE_X;
    }

    public boolean isAxisY() {
        return this == POSITIVE_Y || this == NEGATIVE_Y;
    }

    public boolean isAxisZ() {
        return this == POSITIVE_Z || this == NEGATIVE_Z;
    }

}
