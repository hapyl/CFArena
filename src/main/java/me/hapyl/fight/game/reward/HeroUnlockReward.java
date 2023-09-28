package me.hapyl.fight.game.reward;

import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HeroUnlockReward extends Reward {

    private final Heroes hero;

    public HeroUnlockReward(Heroes hero) {
        super(hero.getName() + " Unlock");

        this.hero = hero;
    }

    public Heroes getHero() {
        return hero;
    }

    @Override
    public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        builder.addLore(BULLET + ChatColor.GOLD + hero.getNameSmallCaps() + " hero unlocked");
    }

    @Override
    public void grantReward(@Nonnull Player player) {
    }

    @Override
    public void revokeReward(@Nonnull Player player) {
    }
}
