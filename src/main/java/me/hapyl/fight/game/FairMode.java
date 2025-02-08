package me.hapyl.fight.game;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;

import javax.annotation.Nonnull;

public enum FairMode implements Described {

    UNFAIR(true),

    MASTERY_ROOKIE,
    MASTERY_I,
    MASTERY_II,
    MASTERY_III,
    MASTERY_IV,
    MASTERY_MASTER;

    private final String name;
    private final String description;
    private final String mastery;

    FairMode(boolean unfair) {
        this.name = "Unfair";
        this.description = """
                &7&oAll players use their own Mastery Level.
                """;
        this.mastery = "Unfair";
    }

    FairMode() {
        final int value = getValue();

        this.mastery = HeroMastery.getLevelString(value);
        this.name = mastery;
        this.description = """
                &7&oAll players will use %s&7&o Mastery while in the game.
                """.formatted(mastery);
    }

    @Nonnull
    public String getMastery() {
        return mastery;
    }

    public int getValue() {
        return ordinal() - 1;
    }

    public boolean isUnfair() {
        return this == UNFAIR;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
