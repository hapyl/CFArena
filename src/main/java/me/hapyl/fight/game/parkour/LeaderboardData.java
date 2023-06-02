package me.hapyl.fight.game.parkour;

import com.google.common.collect.Maps;
import me.hapyl.spigotutils.module.parkour.Stats;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class LeaderboardData implements Comparator<Long> {

    private final UUID uuid;
    private final String name;
    private final long completionTime;
    private final Map<Stats.Type, Long> stats;

    public LeaderboardData(UUID uuid, String name, long completionTime) {
        this.uuid = uuid;
        this.name = name;
        this.completionTime = completionTime;
        this.stats = Maps.newHashMap();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public long getStat(Stats.Type type) {
        return stats.getOrDefault(type, 0L);
    }

    public void setStat(Stats.Type type, long value) {
        stats.put(type, value);
    }

    @Override
    public int compare(Long o1, Long o2) {
        return (int) (o1 - o2);
    }

    public boolean hasStats() {
        return !stats.isEmpty();
    }
}
