package me.hapyl.fight.script;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.util.DependencyInjector;

import javax.annotation.Nonnull;
import java.util.Map;

public class ScriptManager extends DependencyInjector<Main> {

    private final Map<String, ScriptRunner> runningScripts;

    public ScriptManager(Main plugin) {
        super(plugin);

        this.runningScripts = Maps.newHashMap();
    }

    public ScriptRunner run(@Nonnull Script script) {
        final String id = script.getId();

        if (runningScripts.containsKey(id)) {
            return runningScripts.get(id);
        }

        final ScriptRunner runner = new ScriptRunner(script);
        runningScripts.put(id, runner);

        return runner;
    }

    public boolean free(@Nonnull Script script) {
        return runningScripts.remove(script.getId()) != null;
    }

    public boolean isRunning(@Nonnull Script script) {
        return runningScripts.containsKey(script.getId());
    }

}
