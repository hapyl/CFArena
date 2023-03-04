package me.hapyl.fight.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.hapyl.fight.Main;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class DatabaseMongo {

    private final FileConfiguration config;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> players;
    private MongoCollection<Document> settings;

    public DatabaseMongo() {
        this.config = Main.getPlugin().getConfig();

        // Suppress logging
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(java.util.logging.Level.OFF);

        //loadClasses();
    }

    public void stopConnection() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void loadClasses() {
        try {
            Class.forName("com.mongodb.internal.session.ServerSessionPool");
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
}
