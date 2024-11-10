package me.hapyl.fight.game.cosmetic;

/**
 * @deprecated legacy
 */
@Deprecated
public interface RandomDrop {

    float getDropChance();

    default boolean isDroppable() {
        return getDropChance() > 0.0f;
    }

    default String getDropChanceString() {
        return "%.1f%%".formatted(getDropChance() * 100);
    }

}
