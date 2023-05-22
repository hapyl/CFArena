package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.spigotutils.module.math.Numbers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents an achievement that can be completed multiple times.
 */
public class ProgressAchievement extends Achievement {

    /**
     * Max complete times are limited to 8 because of the GUI layout.
     */
    public static final int MAX_COMPLETE = 8;

    @Nullable
    private int[] requirements;

    public ProgressAchievement(@Nullable String id, @Nonnull String name, @Nonnull String description) {
        super(id, name, description);
    }

    public ProgressAchievement(@Nonnull String name, @Nonnull String description, @Nonnull int... requirements) {
        this(null, name, description, requirements);
    }

    public ProgressAchievement(@Nullable String id, @Nonnull String name, @Nonnull String description, @Nonnull int... requirements) {
        this(id, name, description.replace("{}", toString(requirements)));

        this.requirements = requirements;
    }

    @Override
    public ProgressAchievement setReward(Reward reward) {
        return setReward(1, reward);
    }

    public ProgressAchievement setReward(int requirement, Reward reward) {
        this.rewards.put(requirement, reward);
        this.maxCompleteCount = Numbers.clamp(requirement, maxCompleteCount, MAX_COMPLETE);

        if (getRequirementCount() > MAX_COMPLETE) {
            Debug.warn("%s tried to set reward for requirement that exceeds the max!", getName());
        }

        return this;
    }

    public ProgressAchievement forEachRequirement(Consumer<Integer> consumer) {
        if (requirements != null) {
            for (int requirement : requirements) {
                consumer.accept(requirement);
            }
        }

        return this;
    }

    public ProgressAchievement forEachRequirement(BiConsumer<ProgressAchievement, Integer> consumer) {
        if (requirements != null) {
            for (int i : requirements) {
                consumer.accept(this, i);
            }
        }

        return this;
    }

    public int getRequirementCount() {
        return rewards.size();
    }

    @Nonnull
    public static String toString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof int[] array) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < array.length; i++) {
                if (i != 0) {
                    builder.append("/");
                }

                builder.append(array[i]);
            }

            return builder.toString();
        }

        return obj.toString();
    }

}
