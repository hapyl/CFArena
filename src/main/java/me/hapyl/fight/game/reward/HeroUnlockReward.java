package me.hapyl.fight.game.reward;

import me.hapyl.fight.game.heroes.Hero;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HeroUnlockReward extends SimpleReward {

    private final Hero hero;

    public HeroUnlockReward(@Nonnull Hero hero) {
        super("%s Hero Unlock".formatted(hero.getName()));

        this.hero = hero;
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }

    @Nonnull
    @Override
    public RewardDescription getDescription(@Nonnull Player player) {
        return RewardDescription.of(ChatColor.GOLD + hero.getNameSmallCaps() + " hero unlocked");
    }

    @Override
    public void grant(@Nonnull Player player) {
    }

    @Override
    public void revoke(@Nonnull Player player) {
    }
}
