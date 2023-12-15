package me.hapyl.fight.database;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class MongoUtils {

    public static void async(@Nonnull MongoCollection<Document> collection, @Nonnull MongoCallback<Document> async) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    async.async(collection);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            async.then(collection);
                        }
                    }.runTask(Main.getPlugin());
                } catch (RuntimeException error) {
                    async.error(collection, error);
                }
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    /**
     * Retrieves a value from a document using a string.
     * <p>
     * String may or may not have segments separated by a dot.
     *
     * @param root - Root document
     * @param path - Path to value
     * @param def  - Default value if not found.
     * @param <T>  - Type of value
     * @return Value or default value if not found.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(@Nonnull final Document root, @Nonnull final String path, @Nullable final T def) {
        final String[] pathSegments = path.split("\\.");
        Document currentNode = root;

        for (int i = 0; i < pathSegments.length - 1; i++) {
            String segment = pathSegments[i];
            Document nextNode = (Document) currentNode.get(segment);

            if (nextNode == null) {
                return def;
            }

            currentNode = nextNode;
        }

        final Object object = currentNode.get(pathSegments[pathSegments.length - 1]);
        return object != null ? (T) object : def;
    }

    /**
     * Sets a value in a document using a string.
     * <p>
     * String may or may doesn't have segments separated by a dot.
     *
     * @param root  - Root document
     * @param path  - Path to value
     * @param value - Value to set
     * @param <T>   - Type of value
     */
    public static <T> void set(@Nonnull final Document root, @Nonnull final String path, @Nullable T value) {
        final String[] pathSegments = path.split("\\.");
        Document currentNode = root;

        for (int i = 0; i < pathSegments.length - 1; i++) {
            String segment = pathSegments[i];
            Document nextNode = (Document) currentNode.get(segment);

            if (nextNode == null) {
                nextNode = new Document();
                currentNode.put(segment, nextNode);
            }

            currentNode = nextNode;
        }

        final String segment = pathSegments[pathSegments.length - 1];

        if (value == null) { // remove if null
            currentNode.remove(segment);
        }
        else {
            currentNode.put(segment, value);
        }
    }

    public static void updateAsync(@Nonnull MongoCollection<Document> collection, @Nonnull Document filter, @Nonnull Bson update) {
        new BukkitRunnable() {
            @Override
            public void run() {
                collection.updateOne(filter, update);
            }
        }.runTaskAsynchronously(Main.getPlugin());

    }
}
