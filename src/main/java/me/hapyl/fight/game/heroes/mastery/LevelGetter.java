package me.hapyl.fight.game.heroes.mastery;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class LevelGetter {

    private final List<HeroMasteryLevel> levels;

    LevelGetter(List<HeroMasteryLevel> levels) {
        this.levels = levels;
    }

    public <C extends HeroMasteryLevel> double getDouble(@Nonnull Class<C> clazz, @Nonnull Function<C, Double> fn) {
        return getOrDefault(clazz, fn, 0.0d);
    }

    public <C extends HeroMasteryLevel & NumberProvider<Double>> double getDouble(@Nonnull Class<C> clazz) {
        return getDouble(clazz, NumberProvider::provideNumber);
    }

    public <C extends HeroMasteryLevel> float getFloat(@Nonnull Class<C> clazz, @Nonnull Function<C, Float> fn) {
        return getOrDefault(clazz, fn, 0.0f);
    }

    public <C extends HeroMasteryLevel & NumberProvider<Float>> float getFloat(@Nonnull Class<C> clazz) {
        return getFloat(clazz, NumberProvider::provideNumber);
    }

    public <C extends HeroMasteryLevel> int getInteger(@Nonnull Class<C> clazz, @Nonnull Function<C, Integer> fn) {
        return getOrDefault(clazz, fn, 0);
    }

    public <C extends HeroMasteryLevel & NumberProvider<Integer>> int getInteger(@Nonnull Class<C> clazz) {
        return getInteger(clazz, NumberProvider::provideNumber);
    }

    public <C extends HeroMasteryLevel> short getShort(@Nonnull Class<C> clazz, @Nonnull Function<C, Short> fn) {
        return getOrDefault(clazz, fn, (short) 0);
    }

    public <C extends HeroMasteryLevel & NumberProvider<Short>> short getShort(@Nonnull Class<C> clazz) {
        return getShort(clazz, NumberProvider::provideNumber);
    }

    public <C extends HeroMasteryLevel, T> T getOrDefault(@Nonnull Class<C> clazz, @Nonnull Function<C, T> fn, T defaultValue) {
        for (HeroMasteryLevel level : levels) {
            if (clazz.isInstance(level)) {
                return fn.apply(clazz.cast(level));
            }
        }

        return defaultValue;
    }
}
