package me.hapyl.fight.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.collection.GlobalConfigCollection;
import me.hapyl.eterna.module.util.DependencyInjector;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

/**
 * I really don't know how a database works or should work;
 * it's my first time working with mongodb.
 * <p>
 * Just using my knowledge of yml files, that's said,
 * I'm not sure if this is the best way to do it.
 * <p>
 * But to document:
 * {@link Database} is the main class that handles the connection to the database.
 * {@link PlayerDatabaseEntry} is an instance of database value, that handles specific fields.
 */
public class Database extends DependencyInjector<Main> {

    private final FileConfiguration config;
    private final NamedDatabase namedDatabase;
    public MongoCollection<Document> friends;
    protected MongoCollection<Document> players;
    protected MongoCollection<Document> parkour;
    protected MongoCollection<Document> heroStats;
    protected MongoCollection<Document> global;
    private MongoClient client;
    private MongoDatabase database;
    private GlobalConfigCollection globalConfig;

    public Database(Main main) {
        super(main);
        this.config = main.getConfig();
        this.namedDatabase = NamedDatabase.byName(config.getString("database.type"));
    }

    public NamedDatabase getNamedDatabase() {
        return namedDatabase;
    }

    public boolean isDevelopment() {
        return namedDatabase.isDevelopment();
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
            final String connectionLink = config.getString("database.connection_link");

            if (connectionLink == null || connectionLink.equals("null")) {
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
            database = client.getDatabase(namedDatabase.getName());

            getPlugin().getLogger().info(getDatabaseString());

            // load collections
            players = database.getCollection("players");
            parkour = database.getCollection("parkour");
            heroStats = database.getCollection("hero_stats");
            friends = database.getCollection("friends");
            global = database.getCollection("global");

            // load async database
            globalConfig = new GlobalConfigCollection(global);
        } catch (RuntimeException e) {
            breakConnectionAndDisablePlugin("Failed to retrieve a database collection!", e);
        }
    }

    public GlobalConfigCollection getGlobalConfig() {
        return globalConfig;
    }

    @Nonnull
    public String getDatabaseString() {
        return "&a&lMONGO &fConnected Database: &6&l" + namedDatabase.name();
    }

    @Nonnull
    public String getDatabaseName() {
        return database.getName();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getPlayers() {
        return players;
    }

    public MongoCollection<Document> getParkour() {
        return parkour;
    }

    public MongoCollection<Document> getHeroStats() {
        return heroStats;
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
