package me.hapyl.fight.database.async;

import me.hapyl.fight.CF;
import me.hapyl.fight.config.EnvironmentProperty;
import me.hapyl.fight.database.NamedCollection;
import org.bson.Document;

import javax.annotation.Nonnull;

public class EnvironmentAsynchronousDocument extends AsynchronousDocument {
    public EnvironmentAsynchronousDocument() {
        super(CF.getServerDatabase().collection(NamedCollection.ENVIRONMENT), new Document("_id", "properties"));
    }

    @Nonnull
    public <T> T getValue(@Nonnull EnvironmentProperty<T> property) {
        return read(property.name(), property.defaultValue());
    }

    public <T> void setValue(@Nonnull EnvironmentProperty<T> property, @Nonnull Object value) {
        write(property.name(), value);
    }

}
