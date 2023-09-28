package me.hapyl.fight.database;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.*;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerDatabase {

    private static final Map<UUID, PlayerDatabase> UUID_DATABASE_MAP = Maps.newConcurrentMap();

    protected final Player player;
    private final Database mongo;
    private final Document filter;
    private final UUID uuid;

    ///////////////////
    // ENTRIES START //
    ///////////////////
    public final HeroEntry heroEntry;
    public final CurrencyEntry currencyEntry;
    public final StatisticEntry statisticEntry;
    public final SettingEntry settingEntry;
    public final ExperienceEntry experienceEntry;
    public final CosmeticEntry cosmeticEntry;
    public final AchievementEntry achievementEntry;
    public final FriendsEntry friendsEntry;
    public final CollectibleEntry collectibleEntry;
    public final DailyRewardEntry dailyRewardEntry;
    public final CrateEntry crateEntry;
    public final DeliveryEntry deliveryEntry;
    /////////////////
    // ENTRIES END //
    /////////////////

    private Document document;

    public PlayerDatabase(UUID uuid) {
        this.uuid = uuid;
        this.mongo = Main.getPlugin().getDatabase();
        this.player = Bukkit.getPlayer(uuid);

        this.filter = new Document("uuid", uuid.toString());

        this.load();

        // Load entries
        this.currencyEntry = new CurrencyEntry(this);
        this.statisticEntry = new StatisticEntry(this);
        this.settingEntry = new SettingEntry(this);
        this.experienceEntry = new ExperienceEntry(this);
        this.cosmeticEntry = new CosmeticEntry(this);
        this.achievementEntry = new AchievementEntry(this);
        this.friendsEntry = new FriendsEntry(this);
        this.collectibleEntry = new CollectibleEntry(this);
        this.heroEntry = new HeroEntry(this);
        this.dailyRewardEntry = new DailyRewardEntry(this);
        this.crateEntry = new CrateEntry(this);
        this.deliveryEntry = new DeliveryEntry(this);

        UUID_DATABASE_MAP.put(uuid, this);
    }

    public PlayerDatabase(Player player) {
        this(player.getUniqueId());
    }

    public Database getMongo() {
        return mongo;
    }

    public Document getDocument() {
        return document;
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

    // entries start

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

    public AchievementEntry getAchievementEntry() {
        return achievementEntry;
    }

    public CollectibleEntry getCollectibleEntry() {
        return collectibleEntry;
    }

    // entries end

    public PlayerRank getRank() {
        final String rankString = document.get("rank", "DEFAULT");

        return Validate.getEnumValue(PlayerRank.class, rankString, PlayerRank.DEFAULT);
    }

    public void setRank(PlayerRank rank) {
        document.put("rank", rank.name());
    }

    public <T> T getValue(String path, T def) {
        return MongoUtils.get(document, path, def);
    }

    public void setValue(String path, Object object) {
        MongoUtils.set(document, path, object);
    }

    public long getLastOnline() {
        return document.get("lastOnline", 0L);
    }

    @Nonnull
    public String getLastOnlineServer() {
        return document.get("lastOnlineServer", "None");
    }

    public final void sync() {
        save();
        load();
    }

    public void save() {
        checkWriteAccess();

        final String playerName = player == null ? uuid.toString() : player.getName();

        document.append("lastOnline", System.currentTimeMillis());
        document.append("lastOnlineServer", CFUtils.getServerIp());

        try {
            //Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            this.mongo.getPlayers().replaceOne(this.filter, this.document);
            //});

            getLogger().info("Successfully saved database for %s.".formatted(playerName));
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("An error occurred whilst trying to save database for %s.".formatted(playerName));
        }
    }

    public void load() {
        final String playerName = getPlayerName();

        try {
            document = mongo.getPlayers().find(filter).first();

            if (document == null) {
                final MongoCollection<Document> players = mongo.getPlayers();
                final Document document = new Document("uuid", uuid.toString());

                if (!Bukkit.getServer().getOnlineMode()) {
                    document.append("offline", true);
                }

                this.document = document;
                players.insertOne(document);
            }

            // Update player name
            if (player != null) {
                document.put("player_name", playerName);
            }

            getLogger().info("Successfully loaded database for %s.".formatted(playerName));
        } catch (Exception error) {
            error.printStackTrace();
            getLogger().severe("An error occurred whilst trying to load database for %s.".formatted(playerName));
        }
    }

    public void update(Bson set) {
        this.mongo.getPlayers().updateOne(this.filter, set);
    }

    private Logger getLogger() {
        return Main.getPlugin().getLogger();
    }

    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull Player player) {
        return getDatabase(player.getUniqueId());
    }

    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull UUID uuid) {
        return CFUtils.getElementOrThrowErrorIfNull(UUID_DATABASE_MAP, uuid, "database does not exist for " + uuid);
    }

    public static boolean removeDatabase(@Nonnull UUID uuid) {
        return UUID_DATABASE_MAP.remove(uuid) != null;
    }

    public static void dumpDatabaseInstanceInConsoleToConfirmThatThereIsNoMoreInstancesAfterRemoveIsCalledButThisIsTemporaryShouldRemoveOnProd() {
        final Logger logger = Main.getPlugin().getLogger();

        logger.info("Dumping database instances:");
        UUID_DATABASE_MAP.forEach((uuid, db) -> {
            logger.info("%s = %s".formatted(uuid, db));
        });
    }

    protected void checkWriteAccess() throws IllegalStateException {
        if (this instanceof ReadOnlyPlayerDatabase) {
            throw new IllegalStateException("Write in read only instance!");
        }
    }

}
