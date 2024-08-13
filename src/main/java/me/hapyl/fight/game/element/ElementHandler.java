package me.hapyl.fight.game.element;

import me.hapyl.fight.game.GameInstance;

import javax.annotation.Nonnull;

public interface ElementHandler extends StrictElementHandler {

    @Override
    default void onStart(@Nonnull GameInstance instance) {
    }

    @Override
    default void onStop(@Nonnull GameInstance instance) {
    }

    @Override
    default void onPlayersRevealed(@Nonnull GameInstance instance) {
    }

}
