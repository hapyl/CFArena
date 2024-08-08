package me.hapyl.fight.game.parkour;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.eterna.module.parkour.Stats;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class LeaderboardData implements Comparator<Long> {

    private final PlayerDatabase database;
    private final UUID uuid;
    private final long completionTime;
    private final Map<Stats.Type, Long> stats;
    private boolean dirty;

    public LeaderboardData(UUID uuid, long completionTime) {
        this.database = PlayerDatabase.getDatabase(uuid);
        this.uuid = uuid;
        this.completionTime = completionTime;
        this.stats = Maps.newHashMap();
        this.dirty = false;
    }

    public void markDirty() {
        dirty = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Nonnull
    public String getName() {
        return database.getName();
    }

    @Nonnull
    public String getNameFormatted() {
        final PlayerRank rank = database.getRank();
        final String prefix = rank.getPrefix();

        return (!prefix.isEmpty() ? prefix + " " : "") + rank.getFormat().nameColor() + getName();
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

    public boolean isDirty() {
        return dirty;
    }
}
