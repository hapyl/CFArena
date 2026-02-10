package me.hapyl.fight.game.parkour;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.parkour.ParkourData;
import me.hapyl.eterna.module.parkour.ParkourStatistics;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.NamedCollection;
import me.hapyl.fight.database.async.AsynchronousDocument;
import me.hapyl.fight.game.Debug;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// This is handled async so whatever not putting using DatabaseCollection.
public class ParkourDatabase extends AsynchronousDocument {

    private final CFParkour parkour;

    public ParkourDatabase(CFParkour parkour) {
        super(CF.getServerDatabase().collection(NamedCollection.PARKOUR), new Document("parkour", parkour.parkourPath()));
        this.parkour = parkour;

        // Remove invalid entries
        final Document players = getPlayers();
        final Set<String> invalidKeys = Sets.newHashSet();

        for (String key : players.keySet()) {
            try {
                final UUID uuid = UUID.fromString(key);
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                // If a serialize has a valid uuid, but has player never played,
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

    public void syncData(ParkourData data) {
        final Document players = getPlayers();
        final Player player = data.getPlayer();
        final String uuid = player.getUniqueId().toString();
        final Document playerDocument = players.get(uuid, new Document());
        final Document statsDocument = playerDocument.get("stats", new Document());

        playerDocument.put("name", player.getName());
        playerDocument.put("time", data.getCompletionTime());
        playerDocument.put("completed", true);

        playerDocument.remove("is_dirty"); // force removes dirty tag if completed after modifications

        final ParkourStatistics dataStats = data.getStats();

        for (ParkourStatistics.Type value : ParkourStatistics.Type.values()) {
            statsDocument.put(value.name().toLowerCase(), dataStats.getStat(value));
        }

        playerDocument.put("stats", statsDocument);
        write("players." + uuid, playerDocument, then -> {
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

    public Map<ParkourStatistics.Type, Long> getStats(UUID uuid) {
        final Document player = getPlayer(uuid);
        final Document stats = player.get("stats", new Document());
        final Map<ParkourStatistics.Type, Long> map = new HashMap<>();

        for (ParkourStatistics.Type value : ParkourStatistics.Type.values()) {
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
