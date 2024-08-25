package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talent;
import org.bson.Document;

public class StatisticEntry extends PlayerDatabaseEntry {

    private static final String PATH_ROOT = "statistic";
    private static final String PATH_HERO_STATS = "hero_stats";
    private static final String PATH_ABILITY_USAGE = "ability_usage";

    public StatisticEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public void fromPlayerStatistic(Hero hero, StatContainer stat) {
        stat.nonNegativeValuesMapped().forEach((statType, value) -> {
            addStat(statType, value);
            addHeroStat(hero, statType, value);
        });

        addStat(StatType.PLAYED, 1);
        addHeroStat(hero, StatType.PLAYED, 1);

        if (stat.isWinner()) {
            addStat(StatType.WINS, 1);
            addHeroStat(hero, StatType.WINS, 1);
        }

        stat.getUsedAbilities().forEach((talent, value) -> addAbilityUsage(hero, talent, value));
    }

    public long getAbilityUsage(Hero hero, Talent talent) {
        return getInDocument(PATH_ROOT)
                .get(PATH_HERO_STATS, new Document())
                .get(hero.getKeyAsString(), new Document())
                .get(PATH_ABILITY_USAGE, new Document())
                .get(talent.getKeyAsString(), 0L);
    }

    public void setAbilityUsage(Hero hero, Talent talent, long value) {
        setValue(
                "statistic.hero_stats." + hero.getKeyAsString() +
                        ".ability_usage." + talent.getKeyAsString(),
                value
        );
    }

    public void addAbilityUsage(Hero hero, Talent talent, long value) {
        setAbilityUsage(hero, talent, getAbilityUsage(hero, talent) + value);
    }

    public double getHeroStat(Hero hero, StatType type) {
        return getInDocument(PATH_ROOT)
                .get(PATH_HERO_STATS, new Document())
                .get(hero.getKeyAsString(), new Document())
                .get(type.name(), 0.0d);
    }

    public void setHeroStat(Hero hero, StatType type, double value) {
        final Document statistic = getInDocument(PATH_ROOT);
        final Document heroStats = statistic.get(PATH_HERO_STATS, new Document());
        final Document document = heroStats.get(hero.getKeyAsString(), new Document());

        document.put(type.name(), value);
        heroStats.put(hero.getKeyAsString(), document);
        statistic.put(PATH_HERO_STATS, heroStats);
    }

    public void addHeroStat(Hero heroes, StatType type, double value) {
        setHeroStat(heroes, type, getHeroStat(heroes, type) + value);
    }

    public double getStat(StatType statisticType) {
        return getInDocument(PATH_ROOT).get(statisticType.name(), 0.0d);
    }

    public void setStat(StatType statisticType, double value) {
        fetchDocument(PATH_ROOT, document -> document.put(statisticType.name(), value));
    }

    public void addStat(StatType statisticType, double value) {
        setStat(statisticType, getStat(statisticType) + value);
    }

}
