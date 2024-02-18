package me.hapyl.fight.database.rank;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.talents.InsteadOfNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RankFormatter {

    @Nonnull
    @InsteadOfNull("Empty String")
    String prefix();

    @Nonnull
    @InsteadOfNull("Empty String")
    Color nameColor();

    @Nonnull
    @InsteadOfNull("Empty String")
    default Color textColor() {
        return Color.WHITE;
    }

    default boolean allowFormatting() {
        return false;
    }

    @Nullable
    default String joinMessage() {
        return "{player} &6wants to fight!";
    }

    @Nullable
    default String leaveMessage() {
        return "{player} &6has fallen!";
    }

}
