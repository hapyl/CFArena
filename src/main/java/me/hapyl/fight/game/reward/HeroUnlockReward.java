package me.hapyl.fight.game.reward;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.heroes.Hero;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HeroUnlockReward extends Reward {

    private final Hero hero;

    public HeroUnlockReward(@Nonnull Hero hero) {
        super(Key.ofString("%s_hero_unlock".formatted(hero.getKeyAsString())), "%s Hero Unlock".formatted(hero.getName()));

        this.hero = hero;
    }

    @Override
    public boolean hasClaimed(@Nonnull Player player) {
        final long level = CF.getDatabase(player).experienceEntry.get(ExperienceEntry.Type.LEVEL);
        return level >= hero.getMinimumLevel();
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }

    @Override
    public void appendDescription(@Nonnull Player player, @Nonnull RewardDescription description) {
        description.append("&6%s hero unlocked".formatted(hero.getNameSmallCaps()));
    }

}
