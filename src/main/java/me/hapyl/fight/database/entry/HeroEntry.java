package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;

import java.util.List;

public class HeroEntry extends PlayerDatabaseEntry {

    public HeroEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public Heroes getSelectedHero() {
        return Validate.getEnumValue(Heroes.class, getDocument("heroes").get("selected", Heroes.ARCHER.name()), Heroes.ARCHER);
    }

    public void setSelectedHero(Heroes hero) {
        fetchDocument("heroes", heroes -> heroes.put("selected", hero.name()));
    }

    public Skins getSkin(Heroes heroes) {
        final Document document = getDocument("heroes");
        final Document skins = document.get("skin", new Document());
        final String selectedSkin = skins.get(heroes.name(), "");

        return Validate.getEnumValue(Skins.class, selectedSkin);
    }

    public void setSkin(Skins skin) {
        fetchDocument("heroes", heroes -> {
            final Document skins = heroes.get("skin", new Document());
            skins.put(skin.getSkin().getHero().name(), skin.name());
            heroes.put("skin", skins);
        });
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
