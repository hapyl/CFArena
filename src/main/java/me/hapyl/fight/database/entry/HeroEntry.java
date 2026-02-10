package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HeroEntry extends PlayerDatabaseEntry {

    public HeroEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "heroes");
    }

    @Nonnull
    public Hero getSelectedHero() {
        return HeroRegistry.ofString(getValue("selected", ""));
    }

    public void setSelectedHero(@Nonnull Hero hero) {
        setValue("selected", hero.getKeyAsString());
    }

    public boolean isPurchased(@Nonnull Hero hero) {
        return getValue("purchased", new ArrayList<>()).contains(hero.getKeyAsString());
    }

    public void addPurchased(@Nonnull Hero hero) {
        fetchDocumentValue("purchased", new ArrayList<>(), list -> {
            list.add(hero.getKeyAsString());
        });
    }

    public void removePurchased(@Nonnull Hero hero) {
        fetchDocumentValue("purchased", new ArrayList<>(), list -> {
            list.remove(hero.getKeyAsString());
        });
    }

    public void setFavourite(@Nonnull Hero hero, boolean flag) {
        fetchDocumentValue("favourite", new ArrayList<>(), list -> {
            if (flag) {
                list.add(hero.getKeyAsString());
            }
            else {
                list.remove(hero.getKeyAsString());
            }
        });
    }

    @Nonnull
    public List<Hero> getFavouriteHeroes() {
        return getFavouriteHeroesKeys()
                .stream()
                .map(HeroRegistry::ofStringOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nonnull
    public List<String> getFavouriteHeroesKeys() {
        return getValue("favourite", new ArrayList<>());
    }

    public boolean isFavourite(Hero heroes) {
        return getFavouriteHeroes().contains(heroes);
    }

}
