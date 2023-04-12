package me.hapyl.fight.database;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.collection.HeroStatsCollection;

/**
 * Player Database is handled differently from the rest of the database.
 */
// FIXME (hapyl): 012, Apr 12, 2023: Why?
public class DatabaseRest {

    public final HeroStatsCollection heroStats;

    public DatabaseRest(Main main) {
        final Database database = main.getDatabase();

        heroStats = new HeroStatsCollection(database.getStats());
    }

    public HeroStatsCollection getHeroStats() {
        return heroStats;
    }

    public void saveAll() {
        heroStats.save();
    }
}
