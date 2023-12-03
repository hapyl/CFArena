package me.hapyl.fight.database.collection;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talents;
import org.bson.Document;

public class HeroStats extends DatabaseCollection {

    private final Heroes heroes;

    public HeroStats(Heroes heroes) {
        super(Main.getPlugin().getDatabase().getHeroStats(), new Document("hero", heroes.name()));

        this.heroes = heroes;
    }

    public double getStat(StatType statisticType) {
        return document.get(statisticType.name(), 0d);
    }

    public void setStat(StatType statisticType, double value) {
        document.put(statisticType.name(), value);
    }

    public void addStat(StatType statisticType, double value) {
        setStat(statisticType, getStat(statisticType) + value);
    }

    public long getAbilityUsage(Talents talent) {
        final Document abilityUsed = document.get("ability_used", new Document());

        return abilityUsed.get(talent.name(), 0L);
    }

    public void addAbilityUsage(Talents talents, long value) {
        setAbilityUsage(talents, getAbilityUsage(talents) + value);
    }

    public void setAbilityUsage(Talents talent, long value) {
        final Document abilityUsed = document.get("ability_used", new Document());

        abilityUsed.put(talent.name(), value);
        document.put("ability_used", abilityUsed);
    }

    public void fromPlayerStatistic(StatContainer stat) {
        stat.nonNegativeValuesMapped().forEach(this::addStat);

        addStat(StatType.PLAYED, 1);

        if (stat.isWinner()) {
            addStat(StatType.WINS, 1);
        }

        stat.getUsedAbilities().forEach(this::addAbilityUsage);
    }
}
