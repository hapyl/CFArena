package me.hapyl.fight.game.heroes;

public interface TickingHero {

    void tick(int tick);

    default int delay() {
        return 0;
    }

    default int period() {
        return 1;
    }


}
