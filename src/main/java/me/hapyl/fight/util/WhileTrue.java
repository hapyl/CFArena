package me.hapyl.fight.util;

public abstract class WhileTrue implements Runnable {

    protected int i;
    private boolean b;

    public WhileTrue() {
        this(Byte.MAX_VALUE);
    }

    public WhileTrue(final int m) {
        while (!b && i++ <= m) {
            run();
        }
    }

    public final void break0() {
        b = true;
    }

}
