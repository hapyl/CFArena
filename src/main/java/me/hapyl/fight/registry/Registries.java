package me.hapyl.fight.registry;

import me.hapyl.fight.fastaccess.FastAccessRegistry;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.artifact.ArtifactRegistry;

import javax.annotation.Nonnull;

public final class Registries {

    private static final FastAccessRegistry FAST_ACCESS = new FastAccessRegistry();
    private static final ArtifactRegistry ARTIFACTS = new ArtifactRegistry();
    private static final AchievementRegistry ACHIEVEMENTS = new AchievementRegistry();

    /**
     * Gets the {@link FastAccessRegistry}.
     */
    @Nonnull
    public static FastAccessRegistry getFastAccess() {
        return FAST_ACCESS;
    }

    /**
     * Gets the {@link ArtifactRegistry}.
     */
    @Nonnull
    public static ArtifactRegistry getArtifacts() {
        return ARTIFACTS;
    }

    /**
     * Gets the {@link AchievementRegistry}.
     */
    @Nonnull
    public static AchievementRegistry getAchievements() {
        return ACHIEVEMENTS;
    }

}
