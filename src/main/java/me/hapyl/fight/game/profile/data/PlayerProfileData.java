package me.hapyl.fight.game.profile.data;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.StringSimilarity;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.profile.PlayerProfile;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This is used at a random collection for stuff, mostly achievements.
 */
public class PlayerProfileData {

    private final PlayerProfile profile;
    private final Map<Achievements, AchievementData> achievementData;

    public String lastMessage;

    public PlayerProfileData(PlayerProfile profile) {
        this.profile = profile;
        this.achievementData = Maps.newHashMap();
    }

    @Nonnull
    public PlayerProfile getProfile() {
        return profile;
    }

    /**
     * Gets the data for the given achievement.
     * Used to store data for achievements that require multiple uses, timings, etc.
     *
     * @param achievement - Achievement.
     * @return the data for the given achievement.
     */
    @Nonnull
    public AchievementData getAchievementData(Achievements achievement) {
        return achievementData.computeIfAbsent(achievement, fn -> new AchievementData(profile, achievement));
    }

    public boolean isLastMessageSimilarTo(String message) {
        if (lastMessage == null || message == null) {
            return false;
        }

        final double similarity = StringSimilarity.similarity(message, lastMessage);

        return similarity >= 0.8d;
    }
}
