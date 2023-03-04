package me.hapyl.fight.database;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.*;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Database {

    private final DatabaseMongo mongo;
    private final Document filter;
    private final Player player;

    private Document config;

    public Database(Player player) {
        this.mongo = Main.getPlugin().getDatabase();
        this.player = player;

        this.filter = new Document("uuid", player.getUniqueId().toString());

        this.loadFile();
        this.loadEntries();
    }

    public static Database getDatabase(Player player) {
        return PlayerProfile.getProfile(player).getDatabase();
    }

    public DatabaseMongo getMongo() {
        return mongo;
    }

    public Document getConfig() {
        return config;
    }

    public Player getPlayer() {
        return player;
    }

    // entries start
    private HeroEntry heroEntry;
    private CurrencyEntry currencyEntry;
    private StatisticEntry statisticEntry;
    private SettingEntry settingEntry;
    private ExperienceEntry experienceEntry;
    private CosmeticEntry cosmeticEntry;

    private void loadEntries() {
        this.heroEntry = new HeroEntry(this);
        this.currencyEntry = new CurrencyEntry(this);
        this.statisticEntry = new StatisticEntry(this);
        this.settingEntry = new SettingEntry(this);
        this.experienceEntry = new ExperienceEntry(this);
        this.cosmeticEntry = new CosmeticEntry(this);
    }

    public ExperienceEntry getExperienceEntry() {
        return experienceEntry;
    }

    public SettingEntry getSettings() {
        return settingEntry;
    }

    public StatisticEntry getStatistics() {
        return statisticEntry;
    }

    public CurrencyEntry getCurrency() {
        return currencyEntry;
    }

    public HeroEntry getHeroEntry() {
        return heroEntry;
    }

    public CosmeticEntry getCosmetics() {
        return cosmeticEntry;
    }

    // entries end
    public Object getValue(String path) {
        return config.get(path);
    }

    public <E> E getValue(String path, Type<E> type) {
        return type.fromObject(getValue(path));
    }

    public void setValue(String path, Object object) {
    }

    public void saveToFile() {
        try {
            //Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            this.mongo.getPlayers().replaceOne(this.filter, this.config);
            //});

            Bukkit.getLogger().info("Successfully saved database for %s.".formatted(player.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("An error occurred whilst trying to save database for %s.".formatted(player.getName()));
        }
    }

    private void loadFile() {
        this.config = this.mongo.getPlayers().find(this.filter).first();

        if (config == null) {
            final MongoCollection<Document> players = this.mongo.getPlayers();
            final Document document = new Document("uuid", player.getUniqueId().toString()).append("player_name", player.getName());
            this.config = document;
            players.insertOne(document);
        }
    }

    private void sendInfo(String info, Object... toReplace) {
        final String format = Chat.format("&e&lDEBUG: &f" + info, toReplace);
        System.out.println(format);
        //Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> player.sendMessage(format));
    }

    public void update(Bson set) {
        this.mongo.getPlayers().updateOne(this.filter, set);
    }
}
