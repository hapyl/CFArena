package me.hapyl.fight.game.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.spigotutils.module.util.DependencyInjector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AchievementRegistry extends DependencyInjector<Main> {

    private final Map<String, Achievement> byId;
    private final Map<Category, List<Achievement>> byCategory;

    public AchievementRegistry(Main plugin) {
        super(plugin);

        byId = Maps.newLinkedHashMap();
        byCategory = Maps.newHashMap();

        // Register
        for (Achievements value : Achievements.values()) {
            register(value.achievement);
        }

        registerAll();
    }

    @Nonnull
    public List<String> listIds() {
        return Lists.newLinkedList(byId.keySet());
    }

    @Nullable
    public Achievement byId(@ForceLowercase String id) {
        id = id.toLowerCase();

        return byId.get(id);
    }

    @Nullable
    public <A extends Achievement> A byId(@ForceLowercase String id, Class<A> clazz) throws IllegalArgumentException {
        final Achievement achievement = byId(id);

        if (achievement == null) {
            return null;
        }

        if (clazz.isInstance(achievement)) {
            return clazz.cast(achievement);
        }

        throw new IllegalArgumentException("%s is not instance of %s.".formatted(achievement.getName(), clazz.getSimpleName()));
    }

    /**
     * Returns copy of all achievements in a category.
     *
     * @param category - Category to get achievements from.
     * @return List of achievements in category.
     */
    public LinkedList<Achievement> byCategory(Category category, boolean progressive) {
        final LinkedList<Achievement> achievements = Lists.newLinkedList(byCategory.getOrDefault(category, Lists.newArrayList()));

        // remove non-progressive
        if (progressive) {
            achievements.removeIf(achievement -> !achievement.isProgressive());
        }
        else {
            achievements.removeIf(Achievement::isProgressive);
        }

        return achievements;
    }

    public LinkedList<Achievement> byCategory(Category category) {
        return Lists.newLinkedList(byCategory.getOrDefault(category, Lists.newArrayList()));
    }

    public boolean isRegistered(@Nonnull Achievement achievement) {
        return byId.containsKey(achievement.getId());
    }

    public void register(@Nonnull Achievement achievement) {
        final String id = achievement.getId();

        if (isRegistered(achievement)) {
            throw new IllegalArgumentException("Achievement with Id %s is already registered!".formatted(id));
        }

        register0(achievement);
    }

    public <T extends Achievement> void register(@Nonnull T t, @Nonnull Consumer<T> consumer) {
        consumer.accept(t);
        register(t);
    }

    public void unregister(@Nonnull Achievement achievement) {
        unregister0(achievement);
    }

    private void registerAll() {
        // Hero progress achievement
        for (Heroes hero : Heroes.playable()) {
            // Play hero
            final String heroName = hero.getName();

            register(new ProgressAchievement(
                    "play_hero_" + hero.name(),
                    heroName + " Enjoyer",
                    "Play as %s {} times.".formatted(heroName),
                    1, 5, 10, 50, 100
            ), achievement -> {
                achievement.setCategory(Category.HERO_PLAYER);
                achievement.forEachRequirement(i -> achievement.setReward(i, CurrencyReward.create().withCoins(10L * i)));
            });

            // Win hero
            register(
                    new ProgressAchievement(
                            "win_hero_" + hero.name(),
                            heroName + " Winner",
                            "Win as %s {} times.".formatted(heroName),
                            1, 5, 10, 50, 100
                    ),
                    achievement -> {
                        achievement.setCategory(Category.HERO_WINNER);
                        achievement.forEachRequirement((ref, i) -> achievement.setReward(i, CurrencyReward.create().withCoins(200L * i)));
                    }
            );
        }
    }

    private void register0(Achievement achievement) {
        byId.put(achievement.getId(), achievement);
        byCategory.computeIfAbsent(achievement.getCategory(), c -> Lists.newArrayList()).add(achievement);
    }

    private void unregister0(Achievement achievement) {
        byId.remove(achievement.getId());
        byCategory.getOrDefault(achievement.getCategory(), Lists.newArrayList()).remove(achievement);
    }

    public static AchievementRegistry current() {
        return Main.getPlugin().achievementRegistry;
    }
}
