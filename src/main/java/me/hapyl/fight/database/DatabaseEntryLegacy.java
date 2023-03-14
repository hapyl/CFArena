package me.hapyl.fight.database;

import com.google.common.collect.Lists;
import me.hapyl.fight.util.ParamFunction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class DatabaseEntryLegacy {

    private final DatabaseLegacy database;

    public DatabaseEntryLegacy(DatabaseLegacy database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public YamlConfiguration getConfig() {
        return this.database.getYaml();
    }

    public Player getPlayer() {
        return this.database.getPlayer();
    }

    public <E> List<String> eToStringList(Collection<E> collection, ParamFunction<String, E> function) {
        final List<String> list = Lists.newArrayList();
        for (E element : collection) {
            list.add(function.execute(element));
        }
        return list;
    }

}
