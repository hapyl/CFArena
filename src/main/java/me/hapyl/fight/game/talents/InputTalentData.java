package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.talents.archive.techie.Talent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stores input talent left/right click data.
 * <p>
 * This class is structured like a builder, with
 * methods that return itself for easier modification.
 */
public class InputTalentData implements Timed, Cooldown {

    @Nonnull protected String action;
    @Nonnull protected String description;

    protected int duration;
    protected int pointGeneration;
    protected Talent.Type type;

    private int cooldown;

    public InputTalentData(boolean isLeft) {
        this(isLeft ? "Left" : "Right");
    }

    public InputTalentData(@Nonnull String action) {
        this.action = action;
        this.description = action;
        this.type = Talent.Type.DAMAGE;
    }

    public InputTalentData setCooldownSec(int sec) {
        return setCooldown(sec * 20);
    }

    public InputTalentData setDurationSec(int sec) {
        return setDuration(sec * 20);
    }

    public void setType(@Nonnull Talent.Type type) {
        this.type = type;
    }

    @Nonnull
    public Talent.Type getType() {
        return type;
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

    public InputTalentData setDescription(@Nonnull String description, @Nullable Object... format) {
        this.description = description.formatted(format);
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

    public void copyDurationAndCooldownFrom(@Nonnull InputTalentData other) {
        this.duration = other.duration;
        this.cooldown = other.cooldown;
    }
}
