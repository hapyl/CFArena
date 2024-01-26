package me.hapyl.fight.database;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.*;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.translate.Language;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.util.Enums;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerDatabase {

    private static final Map<UUID, PlayerDatabase> UUID_DATABASE_MAP = Maps.newConcurrentMap();

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
    public final HotbarLoadoutEntry hotbarEntry;
    public final FastAccessEntry fastAccessEntry;
    public final MetadataEntry metadataEntry;
    public final ArtifactEntry artifactEntry;
    /////////////////
    // ENTRIES END //
    /////////////////

    @Nonnull
    protected final OfflinePlayer player;
    private final Database mongo;
    private final Document filter;
    private final UUID uuid;
    private Document document;

    PlayerDatabase(UUID uuid) {
        this.uuid = uuid;
        this.mongo = Main.getPlugin().getDatabase();
        this.player = Bukkit.getOfflinePlayer(uuid);

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
        this.hotbarEntry = new HotbarLoadoutEntry(this);
        this.fastAccessEntry = new FastAccessEntry(this);
        this.metadataEntry = new MetadataEntry(this);
        this.artifactEntry = new ArtifactEntry(this);
    }

    PlayerDatabase(Player player) {
        this(player.getUniqueId());
    }

    public Database getMongo() {
        return mongo;
    }

    public Document getDocument() {
        return document;
    }

    @Nonnull
    public OfflinePlayer getPlayer() {
        return player;
    }

    @Nonnull
    public String getPlayerName() {
        return getName();
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

    public AchievementEntry getAchievementEntry() {
        return achievementEntry;
    }

    public CollectibleEntry getCollectibleEntry() {
        return collectibleEntry;
    }

    @Nonnull
    public PlayerRank getRank() {
        final String rankString = document.get("rank", "DEFAULT");

        return Validate.getEnumValue(PlayerRank.class, rankString, PlayerRank.DEFAULT);
    }

    @Nonnull
    public Language getLanguage() {
        final String lang = document.get("lang", Language.ENGLISH.name());

        return Enums.byName(Language.class, lang, Language.ENGLISH);
    }

    public void setLanguage(@Nonnull Language language) {
        document.put("lang", language.name());
    }

    public void setRank(@Nonnull PlayerRank rank) {
        document.put("rank", rank.name());
    }

    public <T> T getValue(@Nonnull String path, @Nullable T def) {
        return MongoUtils.get(document, path, def);
    }

    public void setValue(@Nonnull String path, @Nullable Object object) {
        MongoUtils.set(document, path, object);
    }

    public long getLastOnline() {
        return document.get("lastOnline", 0L);
    }

    @Nonnull
    public String getLastOnlineServer() {
        return document.get("lastOnlineServer", "None");
    }

    @Nonnull
    public String getName() {
        return document.get("player_name", "null");
    }

    public void save() {
        final String playerName = player.getName();

        document.append("lastOnline", System.currentTimeMillis());
        document.append("lastOnlineServer", CFUtils.getServerIp());

        try {
            final MongoCollection<Document> players = this.mongo.getPlayers();
            players.replaceOne(this.filter, this.document);

            getLogger().info("Successfully saved database for %s.".formatted(playerName));
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("An error occurred whilst trying to save database for %s.".formatted(playerName));
        }
    }

    public void load() {
        final String playerName = player.getName();

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
            document.put("player_name", playerName);

            getLogger().info("Successfully loaded %s for %s.".formatted(getClass().getSimpleName(), playerName));
        } catch (Exception error) {
            error.printStackTrace();
            getLogger().severe("An error occurred whilst trying to load a database for %s.".formatted(playerName));
        }
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
        PlayerDatabase database = UUID_DATABASE_MAP.get(uuid);

        if (database == null) {
            database = new OfflinePlayerDatabase(uuid);

            UUID_DATABASE_MAP.put(uuid, database);
        }

        return database;
    }

    @Nonnull
    public static PlayerDatabase instantiate(Player player) {
        PlayerDatabase database = UUID_DATABASE_MAP.get(player.getUniqueId());

        if (database == null || database instanceof OfflinePlayerDatabase) {
            database = new PlayerDatabase(player);

            UUID_DATABASE_MAP.put(player.getUniqueId(), database);
        }

        return database;
    }

    public static boolean uninstantiate(@Nonnull UUID uuid) {
        return UUID_DATABASE_MAP.remove(uuid) != null;
    }
}
