package me.hapyl.fight.database.legacy;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.DatabaseLegacy;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.util.Validate;

import java.util.List;

public class HeroEntryLegacy extends HeroEntry {
    public HeroEntryLegacy(DatabaseLegacy database) {
        super(database);
    }

    public Heroes getSelectedHero() {
        final String string = this.getConfigLegacy().getString("selected-hero", "ARCHER");
        return Validate.getEnumValue(Heroes.class, string);
    }

    public void setSelectedHero(Heroes hero) {
        this.getConfigLegacy().set("selected-hero", hero.name());
    }

    public void setFavourite(Heroes hero, boolean flag) {
        final List<String> favouriteHeroes = getFavouriteHeroesStrings();
        if (flag) {
            favouriteHeroes.add(hero.name());
        }
        else {
            favouriteHeroes.remove(hero.name());
        }
        getConfigLegacy().set("favourite-heroes", favouriteHeroes);
    }

    public List<Heroes> getFavouriteHeroes() {
        final List<Heroes> heroesList = Lists.newArrayList();
        for (String names : getFavouriteHeroesStrings()) {
            heroesList.add(Validate.getEnumValue(Heroes.class, names));
        }
        return heroesList;
    }

    public List<String> getFavouriteHeroesStrings() {
        return getConfigLegacy().getStringList("favourite-heroes");
    }

    public boolean isFavourite(Heroes heroes) {
        return getFavouriteHeroes().contains(heroes);
    }

}
