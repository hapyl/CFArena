package me.hapyl.fight.database;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.fight.Main;
import me.hapyl.fight.config.CFConfig;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * I really don't know how a database works or should work;
 * it's my first time working with mongodb.
 * <p>
 * Just using my knowledge of yml files, that's said,
 * I'm not sure if this is the best way to do it.
 * <p>
 * But to serialize:
 * {@link Database} is the main class that handles the connection to the database.
 * {@link PlayerDatabaseEntry} is an instance of database value, that handles specific fields.
 */
public class Database extends DependencyInjector<@NotNull Main> {

    private static final Pattern CLINK_PATTERN = Pattern.compile("srv://(.*?):");
    private static final String DATABASE_NAME = "classes_fight";

    private final CFConfig config;
    private final Map<NamedCollection, MongoCollection<Document>> collections;

    private MongoClient client;
    private MongoDatabase database;

    public Database(@Nonnull Main main) {
        super(main);

        this.config = main.config();
        this.collections = Maps.newHashMap();
    }

    public void stopConnection() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void createConnection() {
        try {
            final String connectionLink = config.databaseConnectionLink();

            if (connectionLink.equals("null")) {
                breakConnectionAndDisablePlugin("Provide a valid connection link in config.yml!", null);
                return;
            }

            try {
                client = MongoClients.create(connectionLink);
            } catch (RuntimeException e) {
                breakConnectionAndDisablePlugin("Failed to connect to MongoDB! Invalid connection link?", e);
                return;
            }

            // load database
            database = client.getDatabase(DATABASE_NAME);

            getPlugin().getLogger().info(getDatabaseString());

            // load collections
            for (NamedCollection collection : NamedCollection.values()) {
                collections.put(collection, database.getCollection(collection.id()));
            }
        } catch (RuntimeException e) {
            breakConnectionAndDisablePlugin("Failed to retrieve a database collection!", e);
        }
    }

    @Nonnull
    public String getDatabaseString() {
        return "&a&lMONGOdb &f&oCurrently connected to '%s'.".formatted(connectionName());
    }

    private String connectionName() {
        final Matcher matcher = CLINK_PATTERN.matcher(Objects.requireNonNull(config.databaseConnectionLink()));

        return matcher.find() ? matcher.group(1) : "localhost";
    }

    @Nonnull
    public String getDatabaseName() {
        return database.getName();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Nonnull
    public MongoCollection<Document> collection(@Nonnull NamedCollection collection) {
        return collections.get(collection);
    }

    private void breakConnectionAndDisablePlugin(String message, RuntimeException e) {
        final Main plugin = getPlugin();
        final Logger logger = plugin.getLogger();

        logger.severe("");
        logger.severe("Unable to start the plugin!");
        logger.severe(message);
        logger.severe("");

        Bukkit.getPluginManager().disablePlugin(plugin);

        if (e != null) {
            throw e;
        }
    }
}
