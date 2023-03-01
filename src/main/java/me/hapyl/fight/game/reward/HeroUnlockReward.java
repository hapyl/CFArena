package me.hapyl.fight.game.reward;

import me.hapyl.fight.game.heroes.Heroes;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class HeroUnlockReward extends Reward {

    private final Heroes hero;

    public HeroUnlockReward(Heroes hero) {
        super(hero.getName());

        this.hero = hero;
    }

    public Heroes getHero() {
        return hero;
    }

    @Nullable
    @Override
    public String getDisplay() {
        return "unlocked!";
    }

    @Override
    public void grantReward(Player player) {

    }

    @Override
    public void revokeReward(Player player) {

    }
}
