package me.hapyl.fight.game.parkour;

import com.google.common.collect.Sets;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.collection.AsynchronousDatabase;
import me.hapyl.fight.game.Debug;
import me.hapyl.spigotutils.module.parkour.Data;
import me.hapyl.spigotutils.module.parkour.Stats;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// This is handled async so whatever not putting using DatabaseCollection.
public class ParkourDatabase extends AsynchronousDatabase {

    private final CFParkour parkour;

    public ParkourDatabase(CFParkour parkour) {
        super(Main.getPlugin().getDatabase().getParkour(), new Document("parkour", parkour.parkourPath()));
        this.parkour = parkour;

        // Remove invalid entries
        final Document players = getPlayers();
        final Set<String> invalidKeys = Sets.newHashSet();

        for (String key : players.keySet()) {
            try {
                final UUID uuid = UUID.fromString(key);
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                // If a document has a valid uuid, but has player never played,
                // it means that it was created in offline mode, delete it.
                if (!offlinePlayer.hasPlayedBefore()) {
                    Debug.warn("Removing invalid entry: %s".formatted(key));
                    invalidKeys.add(key);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (!invalidKeys.isEmpty()) {
            invalidKeys.forEach(players::remove);
        }

        write("players", players);
    }

    public void syncData(Data data) {
        final Document players = getPlayers();
        final String uuid = data.get().getUniqueId().toString();
        final Document player = players.get(uuid, new Document());
        final Document stats = player.get("stats", new Document());

        player.put("name", data.get().getName());
        player.put("time", data.getCompletionTime());
        player.put("completed", true);

        player.remove("is_dirty"); // force removes dirty tag if completed after modifications

        final Stats dataStats = data.getStats();

        for (Stats.Type value : Stats.Type.values()) {
            stats.put(value.name().toLowerCase(), dataStats.getStat(value));
        }

        player.put("stats", stats);
        write("players." + uuid, player, then -> {
            // Update leaderboard
            parkour.updateLeaderboardIfExists();
        });
    }

    public long getBestTime(UUID uuid) {
        final Document player = getPlayer(uuid);
        return player.get("time", 0L);
    }

    public boolean hasCompleted(UUID uuid) {
        final Document player = getPlayer(uuid);
        return player.get("completed", false);
    }

    public Map<Stats.Type, Long> getStats(UUID uuid) {
        final Document player = getPlayer(uuid);
        final Document stats = player.get("stats", new Document());
        final Map<Stats.Type, Long> map = new HashMap<>();

        for (Stats.Type value : Stats.Type.values()) {
            map.put(value, stats.get(value.name().toLowerCase(), 0L));
        }

        return map;
    }

    public Document getDocument() {
        return document;
    }

    public Document getPlayers() {
        return document.get("players", new Document());
    }

    public Document getPlayer(UUID uuid) {
        return getPlayers().get(uuid.toString(), new Document());
    }


}
