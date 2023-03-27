package me.hapyl.fight.database.collection;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.database.StatisticType;
import me.hapyl.fight.game.StatContainer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talents;
import org.bson.Document;

/**
 * "type": "hero_stats",
 * HERO: {
 * "played": 0,
 * "kills": 0,
 * "deaths": 0,
 * "ability_used": {
 * NAME: times,
 * ...
 * }
 * }
 */
public class HeroStatsCollection extends DatabaseCollection {

    public HeroStatsCollection(MongoCollection<Document> collection) {
        super(collection, new Document("type", "hero_stats"));
    }

    private Document getHeroDocument(Heroes heroes) {
        return document.get(heroes.name(), new Document());
    }

    public double getStat(Heroes heroes, StatisticType statisticType) {
        return getHeroDocument(heroes).get(statisticType.name(), 0d);
    }

    public void setStat(Heroes heroes, StatisticType statisticType, double value) {
        final Document stats = getHeroDocument(heroes);

        stats.put(statisticType.name(), value);
        document.put(heroes.name(), stats);
    }

    public void addStat(Heroes heroes, StatisticType statisticType, double value) {
        setStat(heroes, statisticType, getStat(heroes, statisticType) + value);
    }

    public long getAbilityUsage(Heroes heroes, Talents talent) {
        final Document stats = getHeroDocument(heroes);
        final Document abilityUsed = stats.get("ability_used", new Document());

        return abilityUsed.get(talent.name(), 0L);
    }

    public void addAbilityUsage(Heroes heroes, Talents talents, long value) {
        setAbilityUsage(heroes, talents, getAbilityUsage(heroes, talents) + value);
    }

    public void setAbilityUsage(Heroes heroes, Talents talent, long value) {
        final Document stats = getHeroDocument(heroes);
        final Document abilityUsed = stats.get("ability_used", new Document());

        abilityUsed.put(talent.name(), value);
        stats.put("ability_used", abilityUsed);
        document.put(heroes.name(), stats);
    }

    public void fromPlayerStatistic(Heroes hero, StatContainer stat) {
        addStat(hero, StatisticType.PLAYED, 1);
        addStat(hero, StatisticType.KILLS, stat.getValue(StatContainer.Type.KILLS));
        addStat(hero, StatisticType.DEATHS, stat.getValue(StatContainer.Type.DEATHS));
        addStat(hero, StatisticType.DAMAGE_DEALT, stat.getValue(StatContainer.Type.DAMAGE_DEALT));
        addStat(hero, StatisticType.DAMAGE_TAKEN, stat.getValue(StatContainer.Type.DAMAGE_TAKEN));
        addStat(hero, StatisticType.ULTIMATE_USED, stat.getValue(StatContainer.Type.ULTIMATE_USED));

        if (stat.isWinner()) {
            addStat(hero, StatisticType.WINS, 1);
        }

        stat.getUsedAbilities().forEach((talent, integer) -> addAbilityUsage(hero, talent, integer));
    }


}
