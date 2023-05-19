package me.hapyl.fight.game.reward;

import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
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
        builder.addLore("- &6" + hero.getName() + " hero unlock");

        //if (!heroesUnlock.isEmpty()) {
        //    builder.addSmartLore("&7- &6" + heroesUnlock.stream()
        //            .map(Heroes::getName)
        //            .collect(Collectors.joining("&7, &6"))
        //            .replaceFirst(",([^,]*)$", " &7and$1") + " &7" + (heroesUnlock.size() == 1 ? "hero" : "heroes") + " &7" +
        //            rewards.get(rewards.size() - 1).display(), "  ");
        //}
    }

    @Override
    public void grantReward(@Nonnull Player player) {

    }

    @Override
    public void revokeReward(@Nonnull Player player) {

    }
}
