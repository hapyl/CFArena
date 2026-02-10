package me.hapyl.fight.database;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod;
import me.hapyl.fight.database.entry.*;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.event.custom.PlayerNewbieEvent;
import me.hapyl.fight.game.profile.PlayerDisplay;
import me.hapyl.fight.util.CFUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Logger;

// FIXME @Jun 07, 2025 (xanyjl) -> Do something with OfflinePlayer that thing is dog
public sealed class PlayerDatabase implements Iterable<PlayerDatabaseEntry> permits OfflinePlayerDatabase {
    
    private static final Map<UUID, PlayerDatabase> UUID_DATABASE_MAP = Maps.newConcurrentMap();
    
    // *=* Entries Start *=* //
    
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
    @Deprecated public final CrateEntry crateEntry;
    public final DeliveryEntry deliveryEntry;
    public final HotbarLoadoutEntry hotbarEntry;
    public final FastAccessEntry fastAccessEntry;
    public final MetadataEntry metadataEntry;
    public final ArtifactEntry artifactEntry;
    public final RandomHeroEntry randomHeroEntry;
    public final GuessWhoEntry guessWhoEntry;
    public final ChallengeEntry challengeEntry;
    public final SkinEntry skinEntry;
    public final StoreEntry storeEntry;
    public final QuestEntry questEntry;
    public final CommissionEntry commissionEntry;
    public final WordleEntry wordleEntry;
    
    // *=* Entries End *=* //
    
    // This set is used to store entries, so they call be referenced by an "event"
    private final Set<PlayerDatabaseEntry> entries;
    
    private final Database mongo;
    private final Document filter;
    private final UUID uuid;
    
    private Document document;
    
    PlayerDatabase(UUID uuid) {
        this.uuid = uuid;
        this.mongo = CF.getPlugin().getDatabase();
        
        this.filter = new Document("uuid", uuid.toString());
        this.entries = Sets.newHashSet();
        
        // Load the database
        this.load();
        
        // Load entries
        this.currencyEntry = load(new CurrencyEntry(this));
        this.statisticEntry = load(new StatisticEntry(this));
        this.settingEntry = load(new SettingEntry(this));
        this.experienceEntry = load(new ExperienceEntry(this));
        this.cosmeticEntry = load(new CosmeticEntry(this));
        this.achievementEntry = load(new AchievementEntry(this));
        this.friendsEntry = load(new FriendsEntry(this));
        this.collectibleEntry = load(new CollectibleEntry(this));
        this.heroEntry = load(new HeroEntry(this));
        this.dailyRewardEntry = load(new DailyRewardEntry(this));
        this.crateEntry = load(new CrateEntry(this));
        this.deliveryEntry = load(new DeliveryEntry(this));
        this.hotbarEntry = load(new HotbarLoadoutEntry(this));
        this.fastAccessEntry = load(new FastAccessEntry(this));
        this.metadataEntry = load(new MetadataEntry(this));
        this.artifactEntry = load(new ArtifactEntry(this));
        this.randomHeroEntry = load(new RandomHeroEntry(this));
        this.guessWhoEntry = load(new GuessWhoEntry(this));
        this.challengeEntry = load(new ChallengeEntry(this));
        this.skinEntry = load(new SkinEntry(this));
        this.storeEntry = load(new StoreEntry(this));
        this.questEntry = load(new QuestEntry(this));
        this.commissionEntry = load(new CommissionEntry(this));
        this.wordleEntry = load(new WordleEntry(this));
        
        // Call onLoad
        entries.forEach(PlayerDatabaseEntry::onLoad);
    }
    
    PlayerDatabase(Player player) {
        this(player.getUniqueId());
    }
    
    /**
     * Gets a new {@link PlayerDisplay}.
     *
     * @return player display.
     */
    @Nonnull
    public PlayerDisplay getDisplay() {
        return new PlayerDisplay(this);
    }
    
    @Nonnull
    public Database getMongo() {
        return mongo;
    }
    
    @Nonnull
    public Document getDocument() {
        return Objects.requireNonNull(document, "Database has not yet been loaded!");
    }
    
    @Nonnull
    public Player getPlayer() throws IllegalStateException {
        return Objects.requireNonNull(Bukkit.getPlayer(uuid), "Player is not online!");
    }
    
    @Nonnull
    public String getPlayerName() {
        return playerNameWithFallback();
    }
    
    @Nonnull
    public UUID getUuid() {
        return uuid;
    }
    
    @Nonnull
    public PlayerRank getRank() {
        final String rankString = document.get("rank", "DEFAULT");
        
        return Enums.byName(PlayerRank.class, rankString, PlayerRank.DEFAULT);
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
    public String playerNameWithFallback() {
        return player()
                .map(Player::getName)
                .orElseGet(() -> {
                    final String uuidToString = uuid.toString();
                    
                    return document != null ? document.get("player_name", uuidToString) : uuidToString;
                });
    }
    
    public void save() {
        final String playerName = playerNameWithFallback();
        
        document.append("lastOnline", System.currentTimeMillis());
        document.append("lastOnlineServer", CFUtils.getServerIp());
        
        try {
            // Call onSave
            for (PlayerDatabaseEntry entry : this) {
                entry.onSave();
            }
            
            final MongoCollection<Document> players = this.mongo.collection(NamedCollection.PLAYERS);
            players.replaceOne(this.filter, this.document);
            
            getLogger().info("Saved %s for %s!".formatted(getDatabaseName(), playerName));
        }
        catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("An error occurred whilst trying to save database for %s.".formatted(playerName));
        }
    }
    
    public void load() {
        Validate.isTrue(document == null, "Duplicate database load!");
        
        final MongoCollection<Document> players = mongo.collection(NamedCollection.PLAYERS);
        
        try {
            // Find player's document
            document = players.find(filter).first();
            
            // If it doesn't exist, means player joined for the first time, create one and call event
            if (document == null) {
                players.insertOne(this.document = new Document("uuid", uuid.toString()));
                
                // Call event
                player().ifPresent(player -> new PlayerNewbieEvent(player).callEvent());
            }
            
            // Update player name unless offline
            player().ifPresent(player -> document.put("player_name", player.getName()));
            
            getLogger().info("Loaded %s for %s!".formatted(getDatabaseName(), playerNameWithFallback()));
        }
        catch (Exception error) {
            error.printStackTrace();
            getLogger().severe("An error occurred whilst trying to load a database for %s.".formatted(playerNameWithFallback()));
        }
    }
    
    @Nonnull
    public String getDatabaseName() {
        return getClass().getSimpleName();
    }
    
    @Nonnull
    @Override
    public Iterator<PlayerDatabaseEntry> iterator() {
        return entries.iterator();
    }
    
    @Nonnull
    public Optional<Player> player() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
    
    private <T extends PlayerDatabaseEntry> T load(T t) {
        entries.add(t);
        
        return t;
    }
    
    private Logger getLogger() {
        return CF.getPlugin().getLogger();
    }
    
    /**
     * @deprecated prefer CF#getDatabase
     */
    @ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod
    @Deprecated
    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull UUID uuid) {
        PlayerDatabase database = UUID_DATABASE_MAP.get(uuid);
        
        if (database == null) {
            database = new OfflinePlayerDatabase(uuid);
            
            UUID_DATABASE_MAP.put(uuid, database);
        }
        
        return database;
    }
    
    @ApiStatus.Internal
    @Nonnull
    public static PlayerDatabase instantiate(@Nonnull Player player) {
        PlayerDatabase database = UUID_DATABASE_MAP.get(player.getUniqueId());
        
        if (database == null || database instanceof OfflinePlayerDatabase) {
            database = new PlayerDatabase(player);
            
            UUID_DATABASE_MAP.put(player.getUniqueId(), database);
        }
        
        return database;
    }
    
    @ApiStatus.Internal
    public static boolean uninstantiate(@Nonnull UUID uuid) {
        return UUID_DATABASE_MAP.remove(uuid) != null;
    }
}
