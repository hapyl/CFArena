package me.hapyl.fight.game;

import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public enum FairMode implements Described {

    UNFAIR(true),

    MASTERY_ROOKIE,
    MASTERY_I,
    MASTERY_II,
    MASTERY_III,
    MASTERY_IV,
    MASTERY_V,
    MASTERY_VI,
    MASTERY_VII,
    MASTERY_VIII,
    MASTERY_IX,
    MASTERY_MASTER;

    private final String name;
    private final String description;
    private final String mastery;

    FairMode(boolean unfair) {
        this.name = "Unfair";
        this.description = """
                &7&oAll players have their own Mastery Level.
                """;
        this.mastery = "Unfair";
    }

    FairMode() {
        final int value = getValue();

        this.mastery = HeroMastery.getLevelString(value);
        this.name = "&aFair! &6Mastery " + mastery;
        this.description = """
                &7&oAll players will be &6Mastery %s&7&o while in the game.
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
