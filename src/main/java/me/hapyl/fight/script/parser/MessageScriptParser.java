package me.hapyl.fight.script.parser;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptLine;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.spigotutils.module.chat.Chat;

import javax.annotation.Nonnull;

public class MessageScriptParser implements ScriptLineParser {

    @Override
    public ScriptAction parse(@Nonnull ScriptLine line) {
        if (!line.isKeyMatches("chat")) {
            return null;
        }

        return new MessageScriptAction(line.toString());
    }

    static class MessageScriptAction implements ScriptAction {

        private final String message;

        public MessageScriptAction(String message) {
            this.message = message;
        }

        @Override
        public boolean execute(@Nonnull ScriptRunner runner) {
            Chat.broadcast(message);

            return true;
        }

        @Override
        public String toString() {
            return "message=" + message;
        }
    }
}
