package me.hapyl.fight.script.parser;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptException;
import me.hapyl.fight.script.ScriptLine;

import javax.annotation.Nonnull;
import java.util.List;

public interface ScriptLineParser {

    List<ScriptLineParser> parsers = List.of(
            new MessageScriptParser(),
            new WaitScriptParser(),
            new SetBlockParser()
    );

    ScriptAction parse(@Nonnull ScriptLine line);

    @Nonnull
    static ScriptAction parse1(@Nonnull String line) {
        final ScriptLine scriptLine = new ScriptLine(line);

        for (ScriptLineParser parser : parsers) {
            final ScriptAction action = parser.parse(scriptLine);

            if (action != null) {
                return action;
            }
        }

        throw new ScriptException("Could not parse '" + line + "'!");
    }
}
