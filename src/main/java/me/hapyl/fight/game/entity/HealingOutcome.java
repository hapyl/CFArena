package me.hapyl.fight.game.entity;

import me.hapyl.fight.event.custom.GameEntityHealEvent;

import javax.annotation.Nonnull;

public record HealingOutcome(@Nonnull Type type, double healing, double excess) {

    /**
     * Returns true, if the healing was done, either with or without excess healing.
     *
     * @return true, if the healing was done, either with or without excess healing.
     */
    public boolean hasHealed() {
        return type == Type.HEALED || type == Type.HEALED_WITH_EXCESS;
    }

    /**
     * Returns true, if the healing was done and there is excess healing.
     *
     * @return true, if the healing was done and there is excess healing.
     */
    public boolean hasExcessHealing() {
        return type == Type.HEALED_WITH_EXCESS;
    }

    /**
     * Returns true if the healing was cancelled.
     *
     * @return true if the healing was cancelled.
     */
    public boolean hasCancelled() {
        return type == Type.CANCELLED;
    }

    public enum Type {
        /**
         * Healing has occurred normally.
         */
        HEALED,

        /**
         * Healing has occurred normally, but some healing went to excess healing.
         */
        HEALED_WITH_EXCESS,

        /**
         * Healing was cancelled in the {@link GameEntityHealEvent}.
         */
        CANCELLED
    }

    static HealingOutcome ofCancelled(double healing, double excess) {
        return new HealingOutcome(Type.CANCELLED, healing, excess);
    }

    static HealingOutcome of(double healing, double excess) {
        return new HealingOutcome(excess > 0.0 ? Type.HEALED_WITH_EXCESS : Type.HEALED, healing, excess);
    }
}
