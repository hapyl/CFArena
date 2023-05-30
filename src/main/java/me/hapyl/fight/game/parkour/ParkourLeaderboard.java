package me.hapyl.fight.game.parkour;

import com.google.common.collect.Maps;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParkourLeaderboard {

    private final CFParkour parkour;
    private final Location location;
    private final Hologram hologram;

    public ParkourLeaderboard(CFParkour parkour, double x, double y, double z) {
        this(parkour, new Location(parkour.getStart().getLocation().getWorld(), x, y - 3, z));
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
        final LinkedHashMap<String, Long> topPlayers = getTop(5);
        final IntInt i = new IntInt();

        topPlayers.forEach((name, record) -> {
            this.hologram.addLine("&b#%d - &a%s &7%ss".formatted(i.incrementAndGet(), name, formatTime(record)));
        });

        for (int j = i.get(); j < 5; j++) {
            this.hologram.addLine("&b#%d - &8...".formatted(j + 1));
        }

        this.hologram.updateLines();
        this.hologram.showAll();
    }

    private String formatTime(long millis) {
        if (millis >= 60000) {
            return new SimpleDateFormat("mm:ss.SSS").format(millis);
        }
        else {
            return new SimpleDateFormat("ss.SSS").format(millis);
        }
    }

    public long getWorldRecord() {
        final LinkedHashMap<String, Long> top = getTop(1);
        for (Long value : top.values()) {
            return value;
        }

        return 0L;
    }

    public LinkedHashMap<String, Long> getTop(int limit) {
        final Document players = parkour.getDatabase().getPlayers();
        final Map<String, Long> mapped = Maps.newHashMap();

        for (String key : players.keySet()) {
            final Document record = players.get(key, new Document());
            final long time = record.get("time", 0L);
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(key));

            if (time > 0) {
                final String playerName = offlinePlayer.hasPlayedBefore() ? offlinePlayer.getName() : record.get("name", "Unknown");
                final boolean offline = record.get("offline", false);

                // strikethrough offline players
                mapped.put((offline ? "&m" : "") + playerName, time);
            }
        }

        return mapped.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
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
