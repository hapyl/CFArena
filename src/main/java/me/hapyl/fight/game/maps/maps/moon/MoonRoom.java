package me.hapyl.fight.game.maps.maps.moon;

public abstract class MoonRoom {
    public final int gate;

    public MoonRoom(int gate) {
        this.gate = gate;
    }

    public abstract void open();

    public abstract void close();
}
