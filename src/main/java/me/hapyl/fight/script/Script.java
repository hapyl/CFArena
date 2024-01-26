package me.hapyl.fight.script;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.registry.PatternId;
import me.hapyl.fight.script.parser.ScriptLineParser;
import org.bukkit.command.CommandExecutor;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Script extends PatternId {

    public static final Pattern PATTERN = Pattern.compile("^[a-z0-9_]+$");
    public static final String COMMENT_LINE = "#";
    public static final String VAR_LINE = "$";

    private final String path;
    private final LinkedList<ScriptAction> actions;

    public Script(@Nonnull String id) {
        super(PATTERN);

        id = id.replace("\\", "/");

        if (id.contains("/")) {
            final int indexOfPath = id.lastIndexOf("/");

            path = id.substring(0, indexOfPath);
            setId(id.substring(indexOfPath + 1));
        }
        else {
            path = "";
            setId(id);
        }

        this.actions = Lists.newLinkedList();
    }

    @Nonnull
    public String getName() {
        return getId();
    }

    @Nonnull
    public String getNameWithExtension() {
        return getName() + ".script";
    }

    public void load() {
        final String name = getNameWithExtension();
        final File file = new File(getPath(), name);

        try (FileReader fileReader = new FileReader(file)) {
            final BufferedReader reader = new BufferedReader(fileReader);

            String line;

            // Parse lines
            while ((line = reader.readLine()) != null) {
                // Ignore comment lines
                if (line.startsWith(COMMENT_LINE)) {
                    continue;
                }

                // Ignore blank and empty lines as well
                if (line.isBlank() || line.isEmpty()) {
                    continue;
                }

                final ScriptAction action = ScriptLineParser.parse1(line);

                actions.add(action);
            }

            actions.forEach(Debug::info);

        } catch (FileNotFoundException e) {
            throw new ScriptException("Could not read file '" + name + "' because it doesn't exist!");
        } catch (IOException e) {
            throw new ScriptException("Could not read file '" + name + "'! See the console for details.");
        }
    }

    public void execute(@Nonnull CommandExecutor executor) {
    }

    @Nonnull
    public LinkedList<ScriptAction> copyActions() {
        return new LinkedList<>(actions);
    }

    private String getPath() {
        return Main.getPlugin().getDataFolder() + "/scripts/" + path;
    }
}
