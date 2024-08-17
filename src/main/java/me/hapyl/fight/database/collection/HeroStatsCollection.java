package me.hapyl.fight.database.collection;

import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.heroes.PlayerRating;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.util.Numeric;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class HeroStatsCollection extends AsynchronousDatabase {

    private final DatabaseKey key;

    public HeroStatsCollection(@Nonnull DatabaseKey key) {
        super(Main.getPlugin().getDatabase().getHeroStats(), new Document("hero", key.getKey()));

        this.key = key;
    }

    @Nonnull
    public DatabaseKey getKey() {
        return key;
    }

    public double getStat(StatType statisticType) {
        return read(statisticType.name(), 0d);
    }

    public void setStat(StatType statisticType, double value) {
        write(statisticType.name(), value);
    }

    public void addStat(StatType statisticType, double value) {
        setStat(statisticType, getStat(statisticType) + value);
    }

    public long getAbilityUsage(Talents talent) {
        return read("ability_used." + talent.name(), 0L);
    }

    public void addAbilityUsage(Talents talents, long value) {
        setAbilityUsage(talents, getAbilityUsage(talents) + value);
    }

    public void setAbilityUsage(Talents talent, long value) {
        write("ability_used." + talent.name(), value);
    }

    @Nullable
    public PlayerRating getPlayerRating(@Nonnull UUID uuid) {
        return PlayerRating.fromInt(read("player_rating." + uuid, 0));
    }

    public void setPlayerRating(@Nonnull UUID uuid, @Nonnull PlayerRating rating) {
        write("player_rating." + uuid, rating.toInt());
    }

    public boolean hasRated(@Nonnull UUID uuid) {
        return read("player_rating." + uuid, null) != null;
    }

    @Nullable
    public PlayerRating getAverageRating() {
        final Document document = read("player_rating", new Document());

        int rating = 0;
        int rated = 0;

        for (Object object : document.values()) {
            if (!(object instanceof Integer integer)) {
                continue;
            }

            rating += integer;
            rated++;
        }

        return PlayerRating.fromInt((int) Numbers.clamp((double) rating / rated, 1, 10));
    }

    public void fromPlayerStatistic(StatContainer stat) {
        stat.nonNegativeValuesMapped().forEach(this::addStat);

        addStat(StatType.PLAYED, 1);

        if (stat.isWinner()) {
            addStat(StatType.WINS, 1);
        }

        stat.getUsedAbilities().forEach(this::addAbilityUsage);
    }

    @Nonnull
    public Numeric getNumeric(StatType statType) {
        final double value = getStat(statType);

        return switch (statType) {
            case COINS, KILLS, ASSISTS, EXP, DEATHS, ULTIMATE_USED, WINS, PLAYED -> Numeric.of((int) value);
            default -> Numeric.of(value);
        };
    }
}
