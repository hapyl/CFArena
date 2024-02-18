package me.hapyl.fight.script;

import javax.annotation.Nonnull;

public interface ScriptAction {

    boolean execute(@Nonnull ScriptRunner runner);

}
