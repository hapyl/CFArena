package me.hapyl.fight.game.commission;

import me.hapyl.eterna.module.util.BukkitUtils;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface Tier {

    @Nonnull
    String name();

    @Range(from = 1, to = 1_000)
    double expMultiplier();

    @Range(from = 1, to = Commission.MAX_LEVEL)
    double enemyLevelMultiplier();

    @Range(from = 1, to = Long.MAX_VALUE)
    long startingCost();

    @Nonnull
    default String decimalFormat(@Nonnull Function<Tier, Double> fn) {
        return BukkitUtils.decimalFormat(fn.apply(this), "#.##");
    }

    @Nonnull
    static Tier of(
            @Nonnull String name,
            @Range(from = 1, to = 1_000) double expMultiplier,
            @Range(from = 1, to = Commission.MAX_LEVEL) double enemyLevelMultiplier,
            @Range(from = 1, to = Long.MAX_VALUE) long startingCost
    ) {
        return new Tier() {
            @Nonnull
            @Override
            public String name() {
                return name;
            }

            @Override
            public @Range(from = 1, to = 1_000) double expMultiplier() {
                return expMultiplier;
            }

            @Override
            public @Range(from = 1, to = Commission.MAX_LEVEL) double enemyLevelMultiplier() {
                return enemyLevelMultiplier;
            }

            @Override
            public @Range(from = 1, to = Long.MAX_VALUE) long startingCost() {
                return startingCost;
            }
        };
    }

}
