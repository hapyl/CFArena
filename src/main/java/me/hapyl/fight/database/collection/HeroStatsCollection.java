package me.hapyl.fight.database.collection;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.heroes.PlayerRating;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Numeric;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class HeroStatsCollection extends AsynchronousDatabase implements Keyed {

    private final Key key;

    public HeroStatsCollection(@Nonnull Key key) {
        super(CF.getPlugin().getDatabase().getHeroStats(), new Document("hero", key.getKey()));

        this.key = key;
    }

    @Nonnull
    @Override
    public Key getKey() {
        return key;
    }

    public double getStat(@Nonnull StatType statisticType) {
        return read(statisticType.getKeyAsString(), 0.0d);
    }

    public void setStat(@Nonnull StatType statisticType, double value) {
        write(statisticType.getKeyAsString(), value);
    }

    public void addStat(@Nonnull StatType statisticType, double value) {
        setStat(statisticType, getStat(statisticType) + value);
    }

    public long getAbilityUsage(@Nonnull Talent talent) {
        return read("ability_used." + talent.getKey().getKey(), 0L);
    }

    public void addAbilityUsage(@Nonnull Talent talent, long value) {
        setAbilityUsage(talent, getAbilityUsage(talent) + value);
    }

    public void setAbilityUsage(@Nonnull Talent talent, long value) {
        write("ability_used." + talent.getKeyAsString(), value);
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

        return PlayerRating.fromInt((int) Math.clamp((double) rating / rated, 1, 10));
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
