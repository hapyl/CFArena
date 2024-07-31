package me.hapyl.fight.game.heroes.mastery;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public final class LevelGetter {

    private final GamePlayer player;
    private final HeroMastery mastery;

    LevelGetter(GamePlayer player, HeroMastery mastery) {
        this.player = player;
        this.mastery = mastery;
    }

    public <M extends HeroMasteryLevel & NumberProvider<Integer>> int getInt(@Nonnull Class<M> provider) {
        int number = 0;

        for (HeroMasteryLevel level : mastery.unlockedLevels(player)) {
            number += getNumberOrDefault(level, provider, 0);
        }

        return number;
    }

    public <M extends HeroMasteryLevel & NumberProvider<Double>> double getDouble(@Nonnull Class<M> provider) {
        double number = 0;

        for (HeroMasteryLevel level : mastery.unlockedLevels(player)) {
            number += getNumberOrDefault(level, provider, 0.0d);
        }

        return number;
    }

    public <M extends HeroMasteryLevel & NumberProvider<Float>> float getFloat(@Nonnull Class<M> provider) {
        float number = 0;

        for (HeroMasteryLevel level : mastery.unlockedLevels(player)) {
            number += getNumberOrDefault(level, provider, 0.0f);
        }

        return number;
    }

    public <M extends HeroMasteryLevel & NumberProvider<Long>> long getLong(@Nonnull Class<M> provider) {
        long number = 0;

        for (HeroMasteryLevel level : mastery.unlockedLevels(player)) {
            number += getNumberOrDefault(level, provider, 0L);
        }

        return number;
    }

    @Nullable
    public <M extends HeroMasteryLevel, T> T getObject(@Nonnull Class<M> clazz, @Nonnull Function<M, T> fn) {
        for (HeroMasteryLevel level : mastery.unlockedLevels(player)) {
            if (clazz.isInstance(level)) {
                return fn.apply(clazz.cast(level));
            }
        }

        return null;
    }

    private static <N extends Number, T extends NumberProvider<N>> N getNumberOrDefault(Object obj, Class<T> clazz, N def) {
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj).getNumber();
        }

        return def;
    }
}
