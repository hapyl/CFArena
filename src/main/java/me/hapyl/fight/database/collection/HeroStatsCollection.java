package me.hapyl.fight.database.collection;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talents;
import org.bson.Document;

public class HeroStatsCollection extends AsynchronousDatabase {

    private final Heroes heroes;

    public HeroStatsCollection(Heroes heroes) {
        super(Main.getPlugin().getDatabase().getHeroStats(), new Document("hero", heroes.name()));

        this.heroes = heroes;
    }

    public Heroes getHeroes() {
        return heroes;
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
        //        final Document abilityUsed = document.get("ability_used", new Document());
        //
        //        return abilityUsed.get(talent.name(), 0L);
    }

    public void addAbilityUsage(Talents talents, long value) {
        setAbilityUsage(talents, getAbilityUsage(talents) + value);
    }

    public void setAbilityUsage(Talents talent, long value) {
        write("ability_used." + talent.name(), value);
        //        final Document abilityUsed = document.get("ability_used", new Document());
        //
        //        abilityUsed.put(talent.name(), value);
        //        document.put("ability_used", abilityUsed);
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
