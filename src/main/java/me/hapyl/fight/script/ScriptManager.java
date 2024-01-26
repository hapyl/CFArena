package me.hapyl.fight.script;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Map;

public class ScriptManager extends DependencyInjector<Main> {

    private final Map<String, Script> scripts;
    private final String folderPath = Main.getPlugin().getDataFolder() + "\\scripts";

    protected ScriptRunner runner;

    public ScriptManager(Main plugin) {
        super(plugin);

        this.scripts = Maps.newHashMap();
    }

    public boolean run(@Nonnull String name) {
        final Script script = scripts.get(name);

        if (script == null) {
            return false;
        }

        return run(script);
    }

    public boolean run(@Nonnull Script script) {
        if (runner != null) {
            return false;
        }

        runner = new ScriptRunner(script);
        return true;
    }

    public void reload() {
        scripts.clear();

        final File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdir();
        }

        parseFolderAsync(folder);
    }

    public void abandon() {
        if (runner == null) {
            return;
        }

        runner.cancel();
        runner = null;
    }

    private void parseFolderAsync(File folder) {
        if (folder == null) {
            throw new NullPointerException("File cannot be null.");
        }

        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File must be a directory!");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final File[] files = folder.listFiles();

                if (files == null) {
                    return;
                }

                for (File file : files) {
                    if (file.isDirectory()) {
                        parseFolderAsync(file);
                    }
                    else {
                        String path = file.getPath().replace(folderPath + "\\", "").replace("\\", "/");
                        final int index = path.lastIndexOf(".script");

                        if (index == -1) {
                            continue;
                        }

                        path = path.substring(0, index);

                        final Script script = new Script(path);
                        script.load();

                        scripts.put(path, script);
                    }
                }
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }
}
