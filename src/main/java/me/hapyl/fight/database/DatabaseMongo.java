package me.hapyl.fight.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.internal.session.ServerSessionPool;
import me.hapyl.fight.Main;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseMongo {

    private final FileConfiguration config;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> players;
    private MongoCollection<Document> settings;
    private MongoCollection<Document> parkour;
    private MongoCollection<Document> stats;

    public DatabaseMongo() {
        this.config = Main.getPlugin().getConfig();

        // Suppress logging

        loadClasses();
    }

    public void stopConnection() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    // This is needed to prevent NoClassDefFoundError
    private void loadClasses() {
        try {
            new ServerSessionPool(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean createConnection() {
        try {
            final String login = config.getString("database.login");
            final String password = config.getString("database.password");

            client = MongoClients.create("mongodb+srv://%s:%s@hapyl.cjkmgmx.mongodb.net/?retryWrites=true&w=majority".formatted(
                    login,
                    password
            ));

            database = client.getDatabase("classes_fight");
            players = database.getCollection("players");
            settings = database.getCollection("settings");
            parkour = database.getCollection("parkour");
            stats = database.getCollection("stats");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getSettings() {
        return settings;
    }

    public MongoCollection<Document> getPlayers() {
        return players;
    }

    public MongoCollection<Document> getParkour() {
        return parkour;
    }

    public MongoCollection<Document> getStats() {
        return stats;
    }
}
