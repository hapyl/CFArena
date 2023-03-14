package me.hapyl.fight.database.collection;

import com.mongodb.client.MongoCollection;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bson.Document;

public class DatabaseCollection {

    private final MongoCollection<Document> collection;
    private final Document filter;

    public Document document;

    public DatabaseCollection(MongoCollection<Document> collection, Document filter) {
        this.collection = collection;
        this.filter = filter;

        load();
    }

    public void save() {
        collection.replaceOne(filter, document);
    }

    public void saveAsync() {
        Runnables.runAsync(this::save);
    }

    private void load() {
        this.document = collection.find(filter).first();

        if (document == null) {
            document = new Document(filter);
            collection.insertOne(document);
        }
    }
}
