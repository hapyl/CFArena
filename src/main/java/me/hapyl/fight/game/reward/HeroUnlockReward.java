package me.hapyl.fight.game.reward;

import me.hapyl.fight.game.heroes.Hero;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HeroUnlockReward extends Reward {

    private final Hero hero;

    public HeroUnlockReward(Hero hero) {
        super();

        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    @Nonnull
    @Override
    public RewardDisplay getDisplay(@Nonnull Player player) {
        return RewardDisplay.of(ChatColor.GOLD + hero.getNameSmallCaps() + " hero unlocked");
    }

    @Override
    public void grant(@Nonnull Player player) {
    }

    @Override
    public void revoke(@Nonnull Player player) {
    }
}
