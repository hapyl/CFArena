package me.hapyl.fight.game;

import javax.annotation.Nullable;

/**
 * Indicates that this thing, whatever it is, is disabled.
 * <br>
 * Note that each GUI must implement its own check.
 */
public interface Disabled {

    /**
     * An optional reason.
     */
    @Nullable
    default String disableReason() {
        return null;
    }

}
