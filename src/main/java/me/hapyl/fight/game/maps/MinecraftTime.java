package me.hapyl.fight.game.maps;

public enum MinecraftTime {

    DAY(1000),
    MIDNIGHT(18000),
    NIGHT(13000),
    NOON(6000);

    public final int time;

    MinecraftTime(int time) {
        this.time = time;
    }
}
