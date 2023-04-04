package me.hapyl.fight.database;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.*;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

// TODO (hapyl): 003, Apr 3, 2023: Maybe database should be independent of Profile?
public sealed class Database permits DatabaseLegacy {

    private static final Map<UUID, Database> UUID_DATABASE_MAP = Maps.newConcurrentMap();

    private final DatabaseMongo mongo;
    private final Document filter;
    protected final Player player;
    private final UUID uuid;

    private Document config;

    private final boolean legacy;

    @Deprecated
    protected Database(Player player, boolean legacy) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.mongo = null;
        this.filter = null;
        this.legacy = true;
    }

    public Database(UUID uuid) {
        this.uuid = uuid;
        this.mongo = Main.getPlugin().getDatabase();
        this.player = Bukkit.getPlayer(uuid);
        this.legacy = false;

        this.filter = new Document("uuid", uuid.toString());

        this.loadFile();
        this.loadEntries();
    }

    public Database(Player player) {
        this(player.getUniqueId());
    }

    /**
     * @deprecated legacy database not longer supported
     */
    @Deprecated
    public boolean isLegacy() {
        return legacy;
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

    public UUID getUuid() {
        return uuid;
    }

    // entries start
    protected HeroEntry heroEntry;
    protected CurrencyEntry currencyEntry;
    protected StatisticEntry statisticEntry;
    protected SettingEntry settingEntry;
    protected ExperienceEntry experienceEntry;
    protected CosmeticEntry cosmeticEntry;

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
        saveToFile();
        loadFile();
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
