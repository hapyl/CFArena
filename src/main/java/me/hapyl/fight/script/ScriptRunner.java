package me.hapyl.fight.script;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ScriptRunner extends GameTask {

    private final Script script;
    private final LinkedList<ScriptAction> actions;
    private int wait;

    public ScriptRunner(Script script) {
        this.script = script;
        this.actions = script.copyActions();
        this.wait = 0;

        setShutdownAction(ShutdownAction.IGNORE);
        runTaskTimerAsync(0, 1);
    }

    @Nonnull
    public Script getScript() {
        return script;
    }

    public void wait(int wait) {
        this.wait = wait;
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        if (wait > 0) {
            wait--;
            return;
        }

        final ScriptAction action = actions.pollFirst();

        if (action == null) {
            synchronized (this) {
                Main.getPlugin().scriptManager.abandon();
                Debug.info("done!");
            }
            return;
        }

        action.execute(this);
    }

}
