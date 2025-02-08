package me.hapyl.fight.game.task;

public abstract class PersistentTask extends GameTask {

    public PersistentTask() {
        setShutdownAction(ShutdownAction.IGNORE);
    }

}
