package me.hapyl.fight.game.challenge;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.spigotutils.module.chat.Chat;

import javax.annotation.Nonnull;

public class PlayerChallenge implements Described {

    private final ChallengeType type;
    private final int goal;

    private int current;
    private boolean hasClaimedRewards;
    private boolean hasNotified; // Don't serialize if ever migrate to serialization

    public PlayerChallenge(ChallengeType type, int goal) {
        this.type = type;
        this.goal = goal;
    }

    public int getGoal() {
        return goal;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;

        // Don't spam with COMPLETE! each time player joins
        if (current >= goal) {
            hasNotified = true;
        }
    }

    public boolean hasClaimedRewards() {
        return hasClaimedRewards;
    }

    public void setHasClaimedRewards(boolean hasClaimedRewards) {
        this.hasClaimedRewards = hasClaimedRewards;
    }

    @Nonnull
    public ChallengeType getType() {
        return type;
    }

    public double getProgress() {
        return (double) current / goal;
    }

    @Nonnull
    public String getProgressString() {
        if (current >= goal) {
            return hasClaimedRewards ? "&a✔" : Color.M_YELLOW.bold() + "CAN CLAIM!";
        }

        return Chat.makeStringFractional(current, goal);
    }

    public boolean increment() {
        current = Math.min(current + 1, goal);

        // Check progress
        if (current >= goal && !hasNotified) {
            hasNotified = true;
            return true;
        }

        return false;
    }

    public boolean isComplete() {
        return current >= goal;
    }

    @Nonnull
    @Override
    public String getName() {
        return type.getName();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return type.get().getDescription(this);
    }

    @Nonnull
    public String getRarityName() {
        return Chat.capitalize(type.get().getRarity());
    }

    @Nonnull
    public ChallengeRarity getRarity() {
        return type.get().getRarity();
    }

    @Nonnull
    public static PlayerChallenge of(@Nonnull ChallengeType type) {
        return new PlayerChallenge(type, type.get().generateRandomGoal());
    }
}
