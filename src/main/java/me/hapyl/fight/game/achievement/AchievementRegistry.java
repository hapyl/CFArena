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

public class AchievementRegistry extends DependencyInjector<Main> {

    private final Map<String, Achievement> byId;
    private final Map<Category, List<Achievement>> byCategory;

    public AchievementRegistry(Main plugin) {
        super(plugin);

        byId = Maps.newLinkedHashMap();
        byCategory = Maps.newHashMap();

        register();
        registerStatic();
    }

    @Nonnull
    public List<String> listIds() {
        return Lists.newLinkedList(byId.keySet());
    }

    private void register() {
    }

    private void registerStatic() {
        // Play hero achievements
        for (Heroes hero : Heroes.playable()) {
            final ProgressAchievement achievement = new ProgressAchievement("play_hero_" + hero.name(), hero.getName() + " Enjoyer", "Play");
            final long baseCoins = 100;
            final int[] requirements = { 1, 5, 10, 50, 100 };

            for (int requirement : requirements) {
                achievement.setReward(requirement, CurrencyReward.create().withCoins(baseCoins * requirement));
            }

            register(achievement);
        }
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

        byId.put(id, achievement);
    }

    public void unregister(@Nonnull Achievement achievement) {
        byId.remove(achievement.getId());
    }

    private void register0(Achievement achievement) {
        byId.put(achievement.getId(), achievement);
        byCategory.computeIfAbsent(achievement.getCategory(), c -> Lists.newArrayList()).add(achievement);
    }

    private void unregister0(Achievement achievement) {
        byId.remove(achievement.getId());
        byCategory.getOrDefault(achievement.getCategory(), Lists.newArrayList()).remove(achievement);
    }

}
