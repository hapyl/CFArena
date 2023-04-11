package me.hapyl.fight.database;

import com.google.common.collect.Lists;
import me.hapyl.fight.util.ParamFunction;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseEntry {

    private final Database database;

    public DatabaseEntry(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public Document getConfig() {
        return database.getConfig();
    }

    public Player getPlayer() {
        return this.database.getPlayer();
    }

    protected final <T> T getValue(String paths, T def) {
        return MongoUtils.get(getConfig(), paths, def);
    }

    protected final <T> void setValue(String paths, T value) {
        MongoUtils.set(getConfig(), paths, value);
    }

    protected Document getDocument(String path) {
        return getConfig().get(path, new Document());
    }

    protected void fetchDocument(String path, Consumer<Document> consumer) {
        final Document document = getDocument(path);

        consumer.accept(document);
        getConfig().put(path, document);
    }

    public <E> List<String> eToStringList(Collection<E> collection, ParamFunction<String, E> function) {
        final List<String> list = Lists.newArrayList();
        for (E element : collection) {
            list.add(function.execute(element));
        }
        return list;
    }

}
