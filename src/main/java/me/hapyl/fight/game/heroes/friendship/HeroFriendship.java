package me.hapyl.fight.game.heroes.friendship;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.reward.Reward;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class HeroFriendship {

    private final Hero hero;
    private final Map<Integer, Reward> rewards;

    public HeroFriendship(Hero hero) {
        this.hero = hero;
        this.rewards = new HashMap<>(10);
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }
}
