package me.hapyl.fight.database.collection;

import com.mongodb.client.MongoCollection;
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

    public double getStat(Heroes heroes, Type type) {
        return getHeroDocument(heroes).get(type.name(), 0d);
    }

    public void setStat(Heroes heroes, Type type, double value) {
        final Document stats = getHeroDocument(heroes);

        stats.put(type.name(), value);
        document.put(heroes.name(), stats);
    }

    public void addStat(Heroes heroes, Type type, double value) {
        setStat(heroes, type, getStat(heroes, type) + value);
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
        addStat(hero, Type.PLAYED, 1);
        addStat(hero, Type.KILLS, stat.getValue(StatContainer.Type.KILLS));
        addStat(hero, Type.DEATHS, stat.getValue(StatContainer.Type.DEATHS));
        addStat(hero, Type.DAMAGE_DEALT, stat.getValue(StatContainer.Type.DAMAGE_DEALT));
        addStat(hero, Type.DAMAGE_TAKEN, stat.getValue(StatContainer.Type.DAMAGE_TAKEN));
        addStat(hero, Type.ULTIMATE_USED, stat.getValue(StatContainer.Type.ULTIMATE_USED));

        stat.getUsedAbilities().forEach((talent, integer) -> addAbilityUsage(hero, talent, integer));
    }

    public enum Type {
        PLAYED,
        KILLS,
        DEATHS,
        ULTIMATE_USED,
        DAMAGE_DEALT,
        DAMAGE_TAKEN
    }

}
