package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;

import javax.annotation.Nonnull;

public class ScriptActionWait implements ScriptAction {

    private final int ticks;

    ScriptActionWait(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        runner.wait(ticks);
    }

    @Override
    public String toString() {
        return "" + ticks;
    }

}