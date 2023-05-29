package me.hapyl.fight.game.talents;

import javax.annotation.Nonnull;

/**
 * Stores input talent left/right click data.
 * <p>
 * This class is structured like a builder, with
 * methods that return itself for easier modification.
 */
public class InputTalentData {

    @Nonnull protected String action;
    @Nonnull protected String description;

    protected int duration;
    protected int pointGeneration;
    private int cooldown;

    public InputTalentData(boolean isLeft) {
        this(isLeft ? "Left" : "Right");
    }

    public InputTalentData(@Nonnull String action) {
        this.action = action;
        this.description = action;
    }

    public InputTalentData setCooldownSec(int sec) {
        return setCooldown(sec * 20);
    }

    public InputTalentData setDurationSec(int sec) {
        return setDuration(sec * 20);
    }

    @Nonnull
    public String getAction() {
        return action;
    }

    public InputTalentData setAction(@Nonnull String action) {
        this.action = action;
        return this;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    public InputTalentData setDescription(@Nonnull String description) {
        this.description = description;
        return this;
    }

    public int getCooldown() {
        return cooldown;
    }

    public InputTalentData setCooldown(int cooldown) {
        this.cooldown = cooldown;
        this.pointGeneration = Talent.calcPointGeneration(cooldown);
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public InputTalentData setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getPointGeneration() {
        return pointGeneration;
    }

    public InputTalentData setPointGeneration(int pointGeneration) {
        this.pointGeneration = pointGeneration;
        return this;
    }
}
