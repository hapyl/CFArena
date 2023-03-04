package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.DatabaseEntry;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.util.Validate;

import java.util.List;

public class HeroEntry extends DatabaseEntry {

    public HeroEntry(Database database) {
        super(database);
    }

    public Heroes getSelectedHero() {
        return Validate.getEnumValue(Heroes.class, getDocument("heroes").get("selected", Heroes.ARCHER.name()), Heroes.ARCHER);
    }

    public void setSelectedHero(Heroes hero) {
        fetchDocument("heroes", heroes -> heroes.put("selected", hero.name()));
    }

    public void setFavourite(Heroes hero, boolean flag) {
        final List<String> favouriteHeroes = getFavouriteHeroesStrings();
        if (flag) {
            favouriteHeroes.add(hero.name());
        }
        else {
            favouriteHeroes.remove(hero.name());
        }

        fetchDocument("heroes", heroes -> heroes.put("favourite", favouriteHeroes));
    }

    public List<Heroes> getFavouriteHeroes() {
        final List<Heroes> heroesList = Lists.newArrayList();
        for (String names : getFavouriteHeroesStrings()) {
            heroesList.add(Validate.getEnumValue(Heroes.class, names));
        }
        return heroesList;
    }

    public List<String> getFavouriteHeroesStrings() {
        return getDocument("heroes").get("favourite", Lists.newArrayList());
    }

    public boolean isFavourite(Heroes heroes) {
        return getFavouriteHeroes().contains(heroes);
    }

}
