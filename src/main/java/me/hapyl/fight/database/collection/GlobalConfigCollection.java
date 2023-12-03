package me.hapyl.fight.database.collection;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.globalconfig.Configuration;
import org.bson.Document;

public class GlobalConfigCollection extends AsynchronousDatabase {
    public GlobalConfigCollection(MongoCollection<Document> collection) {
        super(collection, new Document("global", "config"));
    }

    public boolean isEnabled(Configuration configuration) {
        return read(configuration.name(), true);
    }

    public void setEnabled(Configuration configuration, boolean value) {
        write(configuration.name(), value);
    }

}
