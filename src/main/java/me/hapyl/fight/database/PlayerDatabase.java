package me.hapyl.fight.database;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.*;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

// TODO (hapyl): 003, Apr 3, 2023: Maybe database should be independent of Profile? and profile should just have a ref to it?
public class PlayerDatabase {

    private static final Map<UUID, PlayerDatabase> UUID_DATABASE_MAP = Maps.newConcurrentMap();

    protected final Player player;
    private final Database mongo;
    private final Document filter;
    private final UUID uuid;
    private final boolean legacy;
    // entries start
    protected HeroEntry heroEntry;
    protected CurrencyEntry currencyEntry;
    protected StatisticEntry statisticEntry;
    protected SettingEntry settingEntry;
    protected ExperienceEntry experienceEntry;
    protected CosmeticEntry cosmeticEntry;
    // entries end
    private Document config;

    public PlayerDatabase(UUID uuid) {
        this.uuid = uuid;
        this.mongo = Main.getPlugin().getDatabase();
        this.player = Bukkit.getPlayer(uuid);
        this.legacy = false;

        this.filter = new Document("uuid", uuid.toString());

        this.load();
        //this.loadEntries(); -> Async in loadFile
    }

    public PlayerDatabase(Player player) {
        this(player.getUniqueId());
    }

    /**
     * @deprecated legacy database not longer supported
     */
    @Deprecated
    public boolean isLegacy() {
        return legacy;
    }

    public Database getMongo() {
        return mongo;
    }

    public Document getConfig() {
        return config;
    }

    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public String getPlayerName() {
        return player == null ? uuid.toString() : player.getName();
    }

    public UUID getUuid() {
        return uuid;
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

    public PlayerRank getRank() {
        final String rankString = config.get("rank", "DEFAULT");

        return Validate.getEnumValue(PlayerRank.class, rankString, PlayerRank.DEFAULT);
    }

    public void setRank(PlayerRank rank) {
        config.put("rank", rank.name());
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

    public final void sync() {
        save();
        load();
    }

    public void save() {
        final String playerName = player == null ? uuid.toString() : player.getName();

        try {
            //Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            this.mongo.getPlayers().replaceOne(this.filter, this.config);
            //});

            getLogger().info("Successfully saved database for %s.".formatted(playerName));
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("An error occurred whilst trying to save database for %s.".formatted(playerName));
        }
    }

    public void update(Bson set) {
        this.mongo.getPlayers().updateOne(this.filter, set);
    }

    private void loadEntries() {
        this.heroEntry = new HeroEntry(this);
        this.currencyEntry = new CurrencyEntry(this);
        this.statisticEntry = new StatisticEntry(this);
        this.settingEntry = new SettingEntry(this);
        this.experienceEntry = new ExperienceEntry(this);
        this.cosmeticEntry = new CosmeticEntry(this);
    }

    private Logger getLogger() {
        return Main.getPlugin().getLogger();
    }

    public void load() {
        final String playerName = getPlayerName();

        try {
            config = mongo.getPlayers().find(filter).first();

            if (config == null) {
                final MongoCollection<Document> players = mongo.getPlayers();
                final Document document = new Document("uuid", uuid).append("player_name", playerName);

                if (!Bukkit.getServer().getOnlineMode()) {
                    document.append("offline", true);
                }

                config = document;
                players.insertOne(document);
            }

            loadEntries();

            getLogger().info("Successfully loaded database for %s.".formatted(playerName));
        } catch (Exception error) {
            error.printStackTrace();
            getLogger().severe("An error occurred whilst trying to load database for %s.".formatted(playerName));
        }
    }

    public static PlayerDatabase getDatabase(Player player) {
        return PlayerProfile.getProfile(player).getDatabase();
    }
}
