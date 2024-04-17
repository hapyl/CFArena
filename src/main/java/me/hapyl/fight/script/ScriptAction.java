package me.hapyl.fight.script;

import javax.annotation.Nonnull;

public interface ScriptAction {

    void execute(@Nonnull ScriptRunner runner);

}
