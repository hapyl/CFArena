package me.hapyl.fight.game.parkour;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.DatabaseMongo;
import me.hapyl.spigotutils.module.parkour.Data;
import me.hapyl.spigotutils.module.parkour.Stats;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkourDatabase {

    private final DatabaseMongo mongo;
    private final CFParkour parkour;
    private final Document filter;
    private Document document;

    public ParkourDatabase(CFParkour parkour) {
        this.mongo = Main.getPlugin().getDatabase();
        this.parkour = parkour;
        this.filter = new Document("parkour", parkour.parkourPath());

        load();
    }

    private void load() {
        document = this.mongo.getParkour().find(filter).first();

        if (document == null) {
            this.mongo.getParkour().insertOne(new Document(filter));
            document = new Document(filter);
        }
    }

    public void syncData(Data data) {
        final Document players = getPlayers();
        final String uuid = data.get().getUniqueId().toString();
        final Document player = players.get(uuid, new Document());
        final Document stats = player.get("stats", new Document());

        player.put("name", data.get().getName());
        player.put("time", data.getCompletionTime());
        player.put("completed", true);

        final Stats dataStats = data.getStats();

        for (Stats.Type value : Stats.Type.values()) {
            stats.put(value.name().toLowerCase(), dataStats.getStat(value));
        }

        player.put("stats", stats);

        mongo.getParkour().updateOne(filter, new Document("$set", new Document("players." + uuid, player)));
        load(); // reload

        // Update leaderboard
        parkour.updateLeaderboardIfExists();
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

    public Document getPlayers() {
        return document.get("players", new Document());
    }

    public Document getPlayer(UUID uuid) {
        return getPlayers().get(uuid.toString(), new Document());
    }


}
