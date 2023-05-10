package me.hapyl.fight.util;

public final class Booleans {

    private Booleans() {
    }

    public static void ifTrue(final boolean condition, final Runnable runnable) {
        if (condition) {
            runnable.run();
        }
    }

    public static void ifFalse(final boolean condition, final Runnable runnable) {
        if (condition) {
            return;
        }

        runnable.run();
    }
}
