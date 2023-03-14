package me.hapyl.fight.game.heroes;

public abstract class Fx {

    private final String name;

    public Fx(String name) {
        this.name = name;
    }

    public Fx() {
        this("$");
    }

    abstract void display();

    public final boolean isUnnamed() {
        return name.equals("$");
    }

    public final String getName() {
        return name;
    }
}
