package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.database.StatisticType;
import me.hapyl.fight.game.StatContainer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talents;
import org.bson.Document;

public class StatisticEntry extends PlayerDatabaseEntry {

    /**
     * {
     * statistic: {
     * kills: 1,
     * deaths: 0,
     * damage_dealt: 0,
     * damage_taken: 0,
     * hero_stats: {
     * HERO: {
     * played: 0,
     * kills: 0,
     * deaths: 0,
     * ability_usage: {
     * ...
     * }
     * }
     * }
     * }
     * }
     */

    private static final String PATH_ROOT = "statistic";
    private static final String PATH_HERO_STATS = "hero_stats";
    private static final String PATH_ABILITY_USAGE = "ability_usage";

    public StatisticEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    // FIXME: 020, Mar 20, 2023 -> Automation is for losers

    public void fromPlayerStatistic(Heroes hero, StatContainer stat) {
        addStat(StatisticType.KILLS, stat.getValue(StatContainer.Type.KILLS));
        addStat(StatisticType.DEATHS, stat.getValue(StatContainer.Type.DEATHS));
        addStat(StatisticType.DAMAGE_DEALT, stat.getValue(StatContainer.Type.DAMAGE_DEALT));
        addStat(StatisticType.DAMAGE_TAKEN, stat.getValue(StatContainer.Type.DAMAGE_TAKEN));
        addStat(StatisticType.ULTIMATE_USED, stat.getValue(StatContainer.Type.ULTIMATE_USED));
        addStat(StatisticType.PLAYED, 1);

        if (stat.isWinner()) {
            addStat(StatisticType.WINS, 1);
        }

        addHeroStat(hero, StatisticType.KILLS, stat.getValue(StatContainer.Type.KILLS));
        addHeroStat(hero, StatisticType.DEATHS, stat.getValue(StatContainer.Type.DEATHS));
        addHeroStat(hero, StatisticType.PLAYED, 1);

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

        //final Document statistic = getDocument(PATH_ROOT);
        //final Document heroStats = statistic.get(PATH_HERO_STATS, new Document());
        //final Document document = heroStats.get(hero.name(), new Document());
        //final Document abilityUsage = document.get(PATH_ABILITY_USAGE, new Document());
        //
        //abilityUsage.put(talent.name(), value);
        //document.put(PATH_ABILITY_USAGE, abilityUsage);
        //heroStats.put(hero.name(), document);
        //statistic.put(PATH_HERO_STATS, heroStats);
    }

    public void addAbilityUsage(Heroes hero, Talents talent, long value) {
        setAbilityUsage(hero, talent, getAbilityUsage(hero, talent) + value);
    }

    public double getHeroStat(Heroes hero, StatisticType type) {
        return getDocument(PATH_ROOT)
                .get(PATH_HERO_STATS, new Document())
                .get(hero.name(), new Document())
                .get(type.name(), 0.0d);
    }

    public void setHeroStat(Heroes heroes, StatisticType type, double value) {
        final Document statistic = getDocument(PATH_ROOT);
        final Document heroStats = statistic.get(PATH_HERO_STATS, new Document());
        final Document document = heroStats.get(heroes.name(), new Document());

        document.put(type.name(), value);
        heroStats.put(heroes.name(), document);
        statistic.put(PATH_HERO_STATS, heroStats);
    }

    public void addHeroStat(Heroes heroes, StatisticType type, double value) {
        setHeroStat(heroes, type, getHeroStat(heroes, type) + value);
    }

    public double getStat(StatisticType statisticType) {
        return getDocument(PATH_ROOT).get(statisticType.name(), 0.0d);
    }

    public void setStat(StatisticType statisticType, double value) {
        fetchDocument(PATH_ROOT, document -> document.put(statisticType.name(), value));
    }

    public void addStat(StatisticType statisticType, double value) {
        setStat(statisticType, getStat(statisticType) + value);
    }

}
