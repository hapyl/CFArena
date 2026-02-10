package me.hapyl.fight.game.talents.himari;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface HimariAction {

    void execute(@Nonnull GamePlayer player);

    default boolean canExecute(@Nonnull GamePlayer player) {
        return true;
    }

}
