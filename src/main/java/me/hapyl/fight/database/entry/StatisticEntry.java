package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talents;
import org.bson.Document;

public class StatisticEntry extends PlayerDatabaseEntry {

    private static final String PATH_ROOT = "statistic";
    private static final String PATH_HERO_STATS = "hero_stats";
    private static final String PATH_ABILITY_USAGE = "ability_usage";

    public StatisticEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public void fromPlayerStatistic(Heroes hero, StatContainer stat) {
        stat.nonNegativeValuesMapped().forEach(this::addStat);

        addStat(StatType.PLAYED, 1);

        if (stat.isWinner()) {
            addStat(StatType.WINS, 1);
        }

        addHeroStat(hero, StatType.KILLS, stat.getValue(StatType.KILLS));
        addHeroStat(hero, StatType.DEATHS, stat.getValue(StatType.DEATHS));
        addHeroStat(hero, StatType.PLAYED, 1);

        stat.getUsedAbilities().forEach((talent, value) -> addAbilityUsage(hero, talent, value));
    }

    public long getAbilityUsage(Heroes hero, Talents talent) {
        return getDocument(PATH_ROOT)
                .get(PATH_HERO_STATS, new Document())
                .get(hero.name(), new Document())
                .get(PATH_ABILITY_USAGE, new Document())
                .get(talent.name(), 0L);
    }

    public void setAbilityUsage(Heroes hero, Talents talent, long value) {
        setValue("statistic.hero_stats." + hero.name() + ".ability_usage." + talent.name(), value);
    }

    public void addAbilityUsage(Heroes hero, Talents talent, long value) {
        setAbilityUsage(hero, talent, getAbilityUsage(hero, talent) + value);
    }

    public double getHeroStat(Heroes hero, StatType type) {
        return getDocument(PATH_ROOT)
                .get(PATH_HERO_STATS, new Document())
                .get(hero.name(), new Document())
                .get(type.name(), 0.0d);
    }

    public void setHeroStat(Heroes heroes, StatType type, double value) {
        final Document statistic = getDocument(PATH_ROOT);
        final Document heroStats = statistic.get(PATH_HERO_STATS, new Document());
        final Document document = heroStats.get(heroes.name(), new Document());

        document.put(type.name(), value);
        heroStats.put(heroes.name(), document);
        statistic.put(PATH_HERO_STATS, heroStats);
    }

    public void addHeroStat(Heroes heroes, StatType type, double value) {
        setHeroStat(heroes, type, getHeroStat(heroes, type) + value);
    }

    public double getStat(StatType statisticType) {
        return getDocument(PATH_ROOT).get(statisticType.name(), 0.0d);
    }

    public void setStat(StatType statisticType, double value) {
        fetchDocument(PATH_ROOT, document -> document.put(statisticType.name(), value));
    }

    public void addStat(StatType statisticType, double value) {
        setStat(statisticType, getStat(statisticType) + value);
    }

}
