package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import org.bson.Document;

import java.util.List;

public class HeroEntry extends PlayerDatabaseEntry {

    public HeroEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public Hero getSelectedHero() {
        return HeroRegistry.ofString(getInDocument("heroes").getString("selected"));
    }

    public boolean isPurchased(Hero hero) {
        return fetchFromDocument("heroes", document -> {
            return document.get("purchased", Lists.newArrayList()).contains(hero.getKey());
        });
    }

    public void addPurchased(Hero hero) {
        fetchDocument("heroes", document -> {
            document.get("purchased", Lists.newArrayList()).add(hero.getKey());
        });
    }

    public void removePurchased(Hero hero) {
        fetchDocument("heroes", document -> {
            document.get("purchased", Lists.newArrayList()).remove(hero.getKey());
        });
    }

    public void setSelectedHero(Hero hero) {
        fetchDocument("heroes", heroes -> heroes.put("selected", hero.getKey()));
    }

    public Skins getSkin(Hero heroes) {
        final Document document = getInDocument("heroes");
        final Document skins = document.get("skin", new Document());
        final String selectedSkin = skins.get(heroes.getKey(), "");

        return Validate.getEnumValue(Skins.class, selectedSkin);
    }

    public void setSkin(Skins skin) {
        fetchDocument("heroes", heroes -> {
            final Document skins = heroes.get("skin", new Document());
            skins.put(skin.getSkin().getHero().getKey(), skin.name());
            heroes.put("skin", skins);
        });
    }

    public void setFavourite(Hero hero, boolean flag) {
        final List<String> favouriteHeroes = getFavouriteHeroesStrings();
        if (flag) {
            favouriteHeroes.add(hero.getKey());
        }
        else {
            favouriteHeroes.remove(hero.getKey());
        }

        fetchDocument("heroes", heroes -> heroes.put("favourite", favouriteHeroes));
    }

    public List<Hero> getFavouriteHeroes() {
        final List<Hero> heroesList = Lists.newArrayList();

        for (String name : getFavouriteHeroesStrings()) {
            heroesList.add(HeroRegistry.ofStringOrNull(name));
        }

        return heroesList;
    }

    public List<String> getFavouriteHeroesStrings() {
        return getInDocument("heroes").get("favourite", Lists.newArrayList());
    }

    public boolean isFavourite(Hero heroes) {
        return getFavouriteHeroes().contains(heroes);
    }

}
