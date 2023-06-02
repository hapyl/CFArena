package me.hapyl.fight.game.parkour;

import com.google.common.collect.Maps;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.parkour.Stats;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParkourLeaderboard {

    private final CFParkour parkour;
    private final Location location;
    private final Hologram hologram;

    public ParkourLeaderboard(CFParkour parkour, double x, double y, double z) {
        this(parkour, new Location(parkour.getStart().getLocation().getWorld(), x, y, z));
    }

    public ParkourLeaderboard(CFParkour parkour, Location location) {
        this.parkour = parkour;
        this.location = location;
        this.hologram = new Hologram().create(location).showAll();

        Runnables.runLater(this::update, 20L);
    }

    public void update() {
        this.hologram.clear();
        this.hologram.addLine("&6&l%s Leaderboard:".formatted(parkour.getName())).addLine("");

        // Append players
        final LinkedHashMap<UUID, LeaderboardData> topPlayers = getTop(5);
        final IntInt i = new IntInt();

        topPlayers.forEach((name, record) -> {
            this.hologram.addLine("&b#%d - &a%s &7%ss".formatted(
                    i.incrementAndGet(),
                    record.getName(),
                    formatTime(record.getCompletionTime())
            ));
        });

        for (int j = i.get(); j < 5; j++) {
            this.hologram.addLine("&b#%d - &8...".formatted(j + 1));
        }

        this.hologram.addLine("&eClick for details");
        this.hologram.updateLines();
        this.hologram.showAll();
    }

    public String formatTime(long millis) {
        if (millis >= 3.6e+6) {
            return new SimpleDateFormat("hh:mm:ss.SSS").format(millis);
        }
        else if (millis >= 60000) {
            return new SimpleDateFormat("mm:ss.SSS").format(millis);
        }
        return new SimpleDateFormat("ss.SSS").format(millis);
    }

    public long getWorldRecord() {
        final LinkedHashMap<UUID, LeaderboardData> top = getTop(1);
        for (LeaderboardData data : top.values()) {
            return data.getCompletionTime();
        }

        return 0L;
    }

    public LinkedHashMap<UUID, LeaderboardData> getTop(int limit) {
        final Document players = parkour.getDatabase().getPlayers();
        final Map<UUID, LeaderboardData> mapped = Maps.newHashMap();

        for (String key : players.keySet()) {
            final Document record = players.get(key, new Document());
            final Document stats = record.get("stats", new Document());
            final UUID uuid = UUID.fromString(key);

            final long time = record.get("time", 0L);
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (time > 0) {
                final String playerName = offlinePlayer.hasPlayedBefore() ? offlinePlayer.getName() : record.get("name", "Unknown");
                final boolean offline = record.get("offline", false);

                // strikethrough offline players
                final LeaderboardData data = new LeaderboardData(uuid, (offline ? "&m" : "") + playerName, time);

                for (Stats.Type value : Stats.Type.values()) {
                    data.setStat(value, stats.get(value.name().toLowerCase(), 0L));
                }

                mapped.put(uuid, data);
            }
        }

        return mapped.entrySet()
                .stream()
                .sorted((a, b) -> (int) (a.getValue().getCompletionTime() - b.getValue().getCompletionTime()))
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Location getLocation() {
        return location;
    }

    public CFParkour getParkour() {
        return parkour;
    }
}
