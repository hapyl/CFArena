package me.hapyl.fight.registry;

import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.fight.Main;
import me.hapyl.fight.fastaccess.FastAccessRegistry;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.artifact.ArtifactRegistry;
import me.hapyl.fight.game.cosmetic.CosmeticRegistry;
import me.hapyl.fight.game.entity.named.NamedEntityRegistry;
import me.hapyl.fight.npc.NPCRegistry;
import me.hapyl.fight.poi.PointOfInterestRegistry;

import javax.annotation.Nonnull;

public final class Registries extends DependencyInjector<Main> {

    private static Registries registry;

    public final FastAccessRegistry fastAccessRegistry;
    public final ArtifactRegistry artifactRegistry;
    public final AchievementRegistry achievementRegistry;
    public final NPCRegistry npcRegistry;
    public final PointOfInterestRegistry poiRegistry;
    public final CosmeticRegistry cosmeticRegistry;
    public final NamedEntityRegistry namedEntityRegistry;

    public Registries(@Nonnull Main main) {
        super(main);

        registry = this;

        this.artifactRegistry = new ArtifactRegistry();
        this.achievementRegistry = new AchievementRegistry();
        this.npcRegistry = new NPCRegistry();
        this.poiRegistry = new PointOfInterestRegistry();
        this.cosmeticRegistry = new CosmeticRegistry();
        this.fastAccessRegistry = new FastAccessRegistry();
        this.namedEntityRegistry = new NamedEntityRegistry();
    }

    /**
     * Gets the {@link FastAccessRegistry}.
     */
    @Nonnull
    public static FastAccessRegistry getFastAccess() {
        return registry.fastAccessRegistry;
    }

    /**
     * Gets the {@link ArtifactRegistry}.
     */
    @Nonnull
    public static ArtifactRegistry getArtifacts() {
        return registry.artifactRegistry;
    }

    /**
     * Gets the {@link AchievementRegistry}.
     */
    @Nonnull
    public static AchievementRegistry getAchievements() {
        return registry.achievementRegistry;
    }

    /**
     * Gets the {@link NPCRegistry}.
     */
    @Nonnull
    public static NPCRegistry getNPCs() {
        return registry.npcRegistry;
    }

    /**
     * Gets the {@link PointOfInterestRegistry}.
     */
    @Nonnull
    public static PointOfInterestRegistry getPointOfInterests() {
        return registry.poiRegistry;
    }

    /**
     * Gets the {@link CosmeticRegistry}.
     */
    @Nonnull
    public static CosmeticRegistry getCosmetics() {
        return registry.cosmeticRegistry;
    }

    /**
     * Gets the {@link NamedEntityRegistry}.
     */
    @Nonnull
    public static NamedEntityRegistry getEntities() {
        return registry.namedEntityRegistry;
    }

}
