package me.hapyl.fight.game.parkour;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.parkour.ParkourStatistics;
import me.hapyl.eterna.module.util.Runnables;
import me.hapyl.fight.game.color.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.Location;

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
        this.hologram = Hologram.ofArmorStand(location);
        
        Runnables.runLater(this::update, 20L);
    }
    
    public void update() {
        this.hologram.setLines(player -> {
            final ComponentList array = ComponentList.empty();
            
            array.append(Component.text("%s Leaderboard".formatted(parkour.getName()), Color.GOLD, TextDecoration.BOLD));
            array.appendEmpty();
            
            final LinkedHashMap<UUID, LeaderboardData> topPlayers = getTop(5);
            
            // wacky fucking way of doing this but I ain't have time nor do I care to rework it -h
            int index = 0;
            for (Map.Entry<UUID, LeaderboardData> entry : topPlayers.entrySet()) {
                final UUID uuid = entry.getKey();
                final LeaderboardData leaderboardData = entry.getValue();
                
                array.append(
                        Component.text("#%s ".formatted(++index), NamedTextColor.AQUA)
                                 .append(Component.text("- ", Color.GRAY))
                                 .append(Component.text("%s ".formatted(Chat.format(leaderboardData.getNameFormatted())), Color.GREEN))
                                 .append(Component.text("%ss%s".formatted(leaderboardData.getTimeFormatted(), Color.GRAY)))
                                 .append(Component.text(uuid == player.getUniqueId() ? " YOU!" : "", Color.GREEN, TextDecoration.BOLD))
                );
            }
            
            // Append ... if less than 5 entries
            for (int i = index; i < 5; i++) {
                array.append(
                        Component.text("#%s ".formatted(index), Color.AQUA)
                                 .append(Component.text("- ", Color.GRAY))
                                 .append(Component.text("...", Color.DARK_GRAY))
                
                );
            }
            
            return array;
        });
        
        this.hologram.showAll();
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
            final Document statsDocument = record.get("stats", new Document());
            final UUID uuid = UUID.fromString(key);
            
            final long time = record.get("time", 0L);
            if (time > 0) {
                // strikethrough offline players
                final LeaderboardData leaderboardData = new LeaderboardData(uuid, time);
                
                if (record.get("is_dirty", false)) {
                    leaderboardData.markDirty();
                }
                
                for (ParkourStatistics.Type type : ParkourStatistics.Type.values()) {
                    leaderboardData.setStat(type, statsDocument.get(type.name().toLowerCase(), 0L));
                }
                
                mapped.put(uuid, leaderboardData);
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
