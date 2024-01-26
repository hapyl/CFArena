package me.hapyl.fight.script.parser;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptException;
import me.hapyl.fight.script.ScriptLine;
import me.hapyl.fight.script.ScriptRunner;

import javax.annotation.Nonnull;

public class WaitScriptParser implements ScriptLineParser {
    @Override
    public ScriptAction parse(@Nonnull ScriptLine line) {
        if (!line.isKeyMatches("wait")) {
            return null;
        }

        final int ticks = line.getValue(1).toInt();

        if (ticks == 0) {
            return null;
        }

        if (ticks < 0 || ticks > 99999) {
            throw new ScriptException("Abnormal wait: " + ticks);
        }

        return new WaitScriptAction(ticks);
    }

    static class WaitScriptAction implements ScriptAction {

        private final int ticks;

        public WaitScriptAction(int ticks) {
            this.ticks = ticks;
        }

        @Override
        public boolean execute(@Nonnull ScriptRunner runner) {
            runner.wait(ticks);
            return true;
        }

        @Override
        public String toString() {
            return "ticks=" + ticks;
        }
    }
}
