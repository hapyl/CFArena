package me.hapyl.fight.game.parkour;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.parkour.ParkourStatistics;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class LeaderboardData implements Comparator<Long> {

    private final PlayerDatabase database;
    private final UUID uuid;
    private final long completionTime;
    private final Map<ParkourStatistics.Type, Long> stats;
    private boolean dirty;

    public LeaderboardData(UUID uuid, long completionTime) {
        this.database = CF.getDatabase(uuid);
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
        return database.playerNameWithFallback();
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

    public long getStat(ParkourStatistics.Type type) {
        return stats.getOrDefault(type, 0L);
    }

    public void setStat(ParkourStatistics.Type type, long value) {
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
    
    public String getTimeFormatted() {
        final String formatted;
        
        if (completionTime >= 3.6e+6) {
            formatted = new SimpleDateFormat("hh:mm:ss.SSS").format(completionTime);
        }
        else if (completionTime >= 60000) {
            formatted = new SimpleDateFormat("mm:ss.SSS").format(completionTime);
        }
        else {
            formatted = new SimpleDateFormat("ss.SSS").format(completionTime);
        }
        
        return isDirty() ? "&m" + formatted : formatted;
    }
}
