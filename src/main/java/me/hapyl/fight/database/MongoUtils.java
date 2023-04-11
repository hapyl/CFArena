package me.hapyl.fight.database;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

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

    public static <T> T get(final Document root, String path, T def) {
        final String[] paths = path.split("\\.");
        Document current = root;

        for (int i = 0; i < paths.length; i++) {
            final String key = paths[i];
            if (i == paths.length - 1) {
                return current.get(key, def);
            }

            current = current.get(key, new Document());
        }
        return def;
    }

    public static <T> void set(final Document root, String path, T value) {
        final String[] paths = path.split("\\.");

        switch (paths.length) {
            case 1 -> root.put(paths[0], value);

            case 2 -> {
                final Document child = root.get(paths[0], new Document());

                child.put(paths[1], value);
                root.put(paths[0], child);
            }

            case 3 -> {
                final Document child = root.get(paths[0], new Document());
                final Document grandChild = child.get(paths[1], new Document());

                grandChild.put(paths[2], value);
                child.put(paths[1], grandChild);
                root.put(paths[0], child);
            }

            case 4 -> {
                final Document child = root.get(paths[0], new Document());
                final Document grandChild = child.get(paths[1], new Document());
                final Document greatGrandChild = grandChild.get(paths[2], new Document());

                greatGrandChild.put(paths[3], value);
                grandChild.put(paths[2], greatGrandChild);
                child.put(paths[1], grandChild);
                root.put(paths[0], child);
            }

            case 5 -> {
                final Document child = root.get(paths[0], new Document());
                final Document grandChild = child.get(paths[1], new Document());
                final Document greatGrandChild = grandChild.get(paths[2], new Document());
                final Document greatGreatGrandChild = greatGrandChild.get(paths[3], new Document());

                greatGreatGrandChild.put(paths[4], value);
                greatGrandChild.put(paths[3], greatGreatGrandChild);
                grandChild.put(paths[2], greatGrandChild);
                child.put(paths[1], grandChild);
                root.put(paths[0], child);
            }

            case 6 -> {
                final Document child = root.get(paths[0], new Document());
                final Document grandChild = child.get(paths[1], new Document());
                final Document greatGrandChild = grandChild.get(paths[2], new Document());
                final Document greatGreatGrandChild = greatGrandChild.get(paths[3], new Document());
                final Document greatGreatGreatGrandChild = greatGreatGrandChild.get(paths[4], new Document());

                greatGreatGreatGrandChild.put(paths[5], value);
                greatGreatGrandChild.put(paths[4], greatGreatGreatGrandChild);
                greatGrandChild.put(paths[3], greatGreatGrandChild);
                grandChild.put(paths[2], greatGrandChild);
                child.put(paths[1], grandChild);
                root.put(paths[0], child);
            }

            case 7 -> {
                final Document child = root.get(paths[0], new Document());
                final Document grandChild = child.get(paths[1], new Document());
                final Document greatGrandChild = grandChild.get(paths[2], new Document());
                final Document greatGreatGrandChild = greatGrandChild.get(paths[3], new Document());
                final Document greatGreatGreatGrandChild = greatGreatGrandChild.get(paths[4], new Document());
                final Document greatGreatGreatGreatGrandChild = greatGreatGreatGrandChild.get(paths[5], new Document());

                greatGreatGreatGreatGrandChild.put(paths[6], value);
                greatGreatGreatGrandChild.put(paths[5], greatGreatGreatGreatGrandChild);
                greatGreatGrandChild.put(paths[4], greatGreatGreatGrandChild);
                greatGrandChild.put(paths[3], greatGreatGrandChild);
                grandChild.put(paths[2], greatGrandChild);
                child.put(paths[1], grandChild);
                root.put(paths[0], child);
            }
        }

    }

}
