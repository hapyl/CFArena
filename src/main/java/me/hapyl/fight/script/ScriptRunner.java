package me.hapyl.fight.script;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ScriptRunner extends GameTask {

    private final Script script;
    private final LinkedList<ScriptAction> actions;
    private int wait;

    public ScriptRunner(@Nonnull Script script) {
        this.script = script;
        this.actions = script.copyActions();
        this.wait = 0;

        setShutdownAction(ShutdownAction.IGNORE);

        // Making this async would add performance, but I don't think it's needed,
        // feel free to make this async if needed, remember to synchronize actions calls.
        runTaskTimer(1, 1);

        script.onStart();
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
            script.onEnd();
            Main.getPlugin().getScriptManager().free(script);

            cancel();
            return;
        }

        action.execute(this);
    }

    public void forceCancel() {
        actions.clear();
        script.onEnd();

        cancel();
    }

}
