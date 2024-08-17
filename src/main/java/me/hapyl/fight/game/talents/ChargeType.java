package me.hapyl.fight.game.talents;

import javax.annotation.Nonnull;

public enum ChargeType {

    /**
     * Ultimate has charge.
     */
    CHARGED,

    /**
     * Ultimate has overcharged.
     */
    OVERCHARGED;

    @Nonnull
    public <T> T value(@Nonnull T charged, @Nonnull T overcharged) {
        return this == CHARGED ? charged : overcharged;
    }

}
