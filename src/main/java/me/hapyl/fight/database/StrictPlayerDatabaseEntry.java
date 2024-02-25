package me.hapyl.fight.database;

import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A stricter and <b>better</b> impl of {@link PlayerDatabaseEntry}.
 */
public class StrictPlayerDatabaseEntry extends PlayerDatabaseEntry {

    protected final Document root;

    protected StrictPlayerDatabaseEntry(@Nonnull PlayerDatabase database, @Nonnull String path) {
        super(database);
        this.root = database.getDocument();
        this.path = path;
    }

    @Nonnull
    public String getPath() {
        return path;
    }

    protected final <T> void setValue(@Nonnull String path, @Nullable T value) {
        MongoUtils.set(root, makeKey(path), value);
    }

    protected final <T> T getValue(@Nonnull String path, @Nullable T def) {
        return MongoUtils.get(root, makeKey(path), def);
    }

    @Nonnull
    protected final Document getDocument(@Nonnull String path) {
        return root.get(makeKey(path), new Document());
    }

    protected final void fetchDocument(@Nonnull String path, @Nonnull Consumer<Document> consumer) {
        final Document document = getValue(path, new Document());

        consumer.accept(document);
        setValue(path, document);
    }

    protected final <T> T fetchFromDocument(@Nonnull String path, @Nonnull Function<Document, T> function) {
        final Document document = getValue(path, new Document());

        return function.apply(document);
    }

    private String makeKey(@Nonnull String paths) {
        return path + "." + paths;
    }

}
