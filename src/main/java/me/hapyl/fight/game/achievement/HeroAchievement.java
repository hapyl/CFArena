package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.registry.Key;

import javax.annotation.Nonnull;

public class HeroAchievement extends Achievement {

    public HeroAchievement(@Nonnull Hero hero, @Nonnull Key key, @Nonnull String name, @Nonnull String description) {
        super(key, name, description);

        setCategory(Category.HERO);
    }

}
