package me.hapyl.fight.game.commission;

import javax.annotation.Nonnull;

public enum EnumTier {

    BEGINNER(Tier.of("&aBeginner", 1d, 1d, 1_000)),
    CHALLENGING(Tier.of("&eChallenging", 1.25d, 5d, 5_000)),
    TOUGH(Tier.of("&6Tough", 3d, 20d, 10_000)),
    GRUELING(Tier.of("&cGrueling", 10d, 40d, 25_000)),
    PUNISHING(Tier.of("&4Punishing", 96d, 80d, 100_000));

    private final Tier tier;

    EnumTier(@Nonnull Tier tier) {
        this.tier = tier;
    }

    @Nonnull
    public Tier tier() {
        return tier;
    }

    @Override
    public String toString() {
        return tier.name();
    }

    @Nonnull
    public static EnumTier of(int intTier) {
        for (EnumTier tier : values()) {
            if (tier.ordinal() + 1 == intTier) {
                return tier;
            }
        }

        return BEGINNER;
    }

}
