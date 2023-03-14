package me.hapyl.fight.database;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.collection.HeroStatsCollection;

/**
 * Player Database is handled differently from the rest of the database.
 */
public class Databases {

    public final HeroStatsCollection heroStats;

    public Databases(Main main) {
        final DatabaseMongo database = main.getDatabase();

        heroStats = new HeroStatsCollection(database.getStats());
    }

    public HeroStatsCollection getHeroStats() {
        return heroStats;
    }

    public void saveAll() {
        heroStats.save();
    }
}
