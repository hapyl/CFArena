package me.hapyl.fight.util;

public final class Mth {

    private Mth() {
    }

    public static int incMn(int n, int i) {
        return Math.min(n + 1, i);
    }

    public static int dcrMx(int n, int i) {
        return Math.max(n - 1, i);
    }

    public static double scale(double v, double i) {
        return v == 0.0d ? 0.0d : v == 1.0d ? i : v * i;
    }
}
