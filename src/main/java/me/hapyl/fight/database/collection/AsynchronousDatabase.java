package me.hapyl.fight.database.collection;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.MongoUtils;
import me.hapyl.fight.database.PlayerDatabase;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This operates differently from {@link PlayerDatabase}, because
 * it is only allowing two operations:
 *
 * <pre>
 * {@link #read(String, Object)} and {@link #write(String, Object)}
 * </pre>
 * <p>
 * Both read and write provides nested support by using a '.' (dot) as path separator.
 * As example:
 *
 * <pre>
 *     read("what.is.my.name", "hapyl");
 * </pre>
 * <p>
 * will read from:
 *
 * <pre>
 *     {
 *         what: {
 *             is: {
 *                 my: {
 *                     name: HERE
 *                 }
 *             }
 *         }
 *     }
 * </pre>
 * <p>
 * Of course, the non-nested path is also supported.
 * <pre>
 *     read("coins", 1);
 * </pre>
 * <p>
 * will read from:
 *
 * <pre>
 *     {
 *         coins: HERE
 *     }
 * </pre>
 *
 * <h2>
 * Violating nesting will throw an error.
 * </h2>
 * <p>
 * Calling:
 *
 * <pre>
 *     read("what.is", "hapyl");
 * </pre>
 * <p>
 * while the document structured like:
 *
 * <pre>
 *     {
 *         what: {
 *             is: {
 *                 my: {
 *                     name: "hapyl"
 *                 }
 *             }
 *         }
 *     }
 * </pre>
 * <p>
 * will cause an error.
 *
 * <p>
 * <p>
 * User may also use {@link #synchronize()} to synchronize local with remote.
 * Which, unless violated, will sync the document remote.
 * </p>
 * <ul>
 *     <li>When <b>reading</b>, the data will be read from the current document.</li>
 *     <li>When <b>writing</b>, the data will be updated instantly and the document will be refreshed. Both locally and on remote. Writing is done asynchronously.</li>
 * </ul>
 */
public class AsynchronousDatabase {

    private final MongoCollection<Document> collection;
    private final Document filter;

    private Document document;

    public AsynchronousDatabase(MongoCollection<Document> collection, Document filter) {
        this.collection = collection;
        this.filter = filter;

        load();
    }

    public final void synchronize() {
        async(this::load);
    }

    /**
     * Reads a value from a given path.
     * Path may or may not be nested by using '.' (dot) as a separator.
     *
     * @param path - Path to read from.
     * @param def  - Default value.
     * @return an element or def.
     */
    public final <E> E read(@Nonnull String path, @Nullable E def) {
        return MongoUtils.get(document, path, def);
    }

    /**
     * Writes a value to a given path.
     * Path may or may not be nested by using '.' (dot) as a separator.
     *
     * @param path  - Path to write to.
     * @param value - Value to write.
     */
    public final <E> void write(@Nonnull String path, @Nullable E value) {
        write(path, value, null);
    }

    /**
     * Writes a value to a given path.
     * Path may or may not be nested by using '.' (dot) as a separator.
     *
     * <b>
     * Writing is done asynchronously.
     * </b>
     *
     * @param path    - Path to write to.
     * @param value   - Value to write.
     * @param andThen - Updated instance of this.
     */
    public final <E> void write(@Nonnull String path, @Nullable E value, @Nullable Consumer<AsynchronousDatabase> andThen) {
        try {
            async(() -> {
                document = collection.findOneAndUpdate(filter, Updates.set(path, value));

                if (document == null) {
                    Main.getPlugin().getLogger().warning("document is null");
                }

                if (andThen != null) {
                    andThen.accept(this);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns keys of this document.
     *
     * @return keys of this document.
     */
    public final Set<String> keyset() {
        return document.keySet();
    }

    private void load() {
        this.document = collection.find(filter).first();

        if (document == null) {
            document = new Document(filter);
            collection.insertOne(document);
        }
    }

    private void async(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }
}
