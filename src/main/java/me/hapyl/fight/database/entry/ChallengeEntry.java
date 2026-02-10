package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.challenge.PlayerChallenge;
import me.hapyl.fight.game.challenge.PlayerChallengeList;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.eterna.module.util.Enums;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * challenge: {
 *     day: 1,
 *     daily: {
 *        0: {
 *          type: kill_players,
 *          goal: 10,
 *          progress: 0.7
 *        }
 *     }
 * }
 */
public class ChallengeEntry extends PlayerDatabaseEntry {

    public ChallengeEntry(@Nonnull PlayerDatabase database) {
        super(database, "challenge");
    }

    public int getDay() {
        return getValue("day", -1);
    }

    public void setDay(int day) {
        setValue("day", day);
    }

    public boolean hasResetToday() {
        return getValue("has_reset_today", false);
    }

    public void markResetToday() {
        setValue("has_reset_today", true);
    }

    public void resetResetToday() {
        setValue("has_reset_today", null);
    }

    public void saveChallenge(int i, @Nullable PlayerChallenge challenge) {
        if (challenge == null) {
            setValue("daily." + i, null);
            return;
        }

        fetchDocument("daily." + i, document -> {
            document.put("type", challenge.getType().getKeyAsString());
            document.put("goal", challenge.getGoal());
            document.put("progress", challenge.getProgress());
            document.put("rewards_claimed", challenge.hasClaimedRewards());
        });
    }

    @Override
    public void onSave() {
        final PlayerProfile profile = getProfile();

        if (profile == null) {
            return;
        }

        final PlayerChallengeList challengeList = profile.getChallengeList();

        setDay(challengeList.getCurrentDay());

        final PlayerChallenge[] challenges = challengeList.getChallenges();

        for (int i = 0; i < challenges.length; i++) {
            saveChallenge(i, challenges[i]);
        }
    }

    @Nullable
    public PlayerChallenge loadChallenge(int i) {
        return fetchFromDocument("daily." + i, document -> {

            final String typeName = document.get("type", "");
            final ChallengeType type = Enums.byName(ChallengeType.class, typeName);

            if (type == null) {
                return null;
            }

            final int goal = document.get("goal", 1);
            final double progress = document.get("progress", 0.0d);
            final boolean rewardClaimed = document.get("rewards_claimed", false);

            final PlayerChallenge challenge = new PlayerChallenge(type, goal);

            challenge.setHasClaimedRewards(rewardClaimed);
            challenge.setCurrent((int) (goal * progress));

            return challenge;
        });
    }

}
