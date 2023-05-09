package me.hapyl.fight.game.reward;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

// FIXME (hapyl): 020, Apr 20, 2023: pluralize
public class HeroUnlockReward extends Reward {

    private final Heroes hero;

    public HeroUnlockReward(Heroes hero) {
        super(hero.getName());

        this.hero = hero;
    }

    public Heroes getHero() {
        return hero;
    }

    @Override
    public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        final List<Heroes> heroesUnlock = Lists.newArrayList();

    }

    @Override
    public void grantReward(@Nonnull Player player) {

    }

    @Override
    public void revokeReward(@Nonnull Player player) {

    }
}
