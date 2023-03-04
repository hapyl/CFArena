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

    public void fetchPaths(String path, Consumer<Document> consumer) {
        final String[] paths = path.split("\\.");
        Document current = getConfig();

        // Reduce for single paths
        if (paths.length == 1) {
            final Document document = getConfig().get(paths[0], new Document());
            consumer.accept(document);
            getConfig().put(paths[0], document);
            return;
        }

        for (int i = 0; i < paths.length - 1; i++) {
            current = current.get(paths[i], new Document());
        }

        Document lastChild = current.get(paths[paths.length - 1], new Document());
        consumer.accept(lastChild);

        for (int i = paths.length - 2; i >= 0; i--) {
            Document child = current.get(paths[i], new Document());
            child.put(paths[i + 1], lastChild);
            lastChild = child;
            current.put(paths[i], lastChild);
        }

        getConfig().put(paths[0], lastChild);
    }

    protected Document getDocument(String path) {
        return getConfig().get(path, new Document());
    }

    protected void fetchDocument(String path, Consumer<Document> consumer) {
        final Document document = getDocument(path);

        consumer.accept(document);
        updateDocument(path);
    }

    protected void updateDocument(String path) {
        getConfig().put(path, getDocument(path));
    }

    public <E> List<String> eToStringList(Collection<E> collection, ParamFunction<String, E> function) {
        final List<String> list = Lists.newArrayList();
        for (E element : collection) {
            list.add(function.execute(element));
        }
        return list;
    }

}
