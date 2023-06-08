package me.hapyl.fight.game.achievement;

import javax.annotation.Nonnull;

/**
 * Represents a hidden achievement.
 * For the most part, a normal achievement but not shown in GUI until unlocked.
 */
public class HiddenAchievement extends Achievement {
    public HiddenAchievement(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    @Nonnull
    @Override
    public String getType() {
        return "Hidden Achievement";
    }
}
