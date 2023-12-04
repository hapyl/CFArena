package me.hapyl.fight.game.task;

public abstract class DelayedGameTask extends GameTask {

    public DelayedGameTask(long delay) {
        runTaskLater(delay);
    }

}
