package me.hapyl.fight.game.element;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface PlayerElementHandler extends StrictPlayerElementHandler {

    @Override
    default void onStart(@Nonnull GamePlayer player) {
    }

    @Override
    default void onStop(@Nonnull GamePlayer player) {
    }

    @Override
    default void onPlayersRevealed(@Nonnull GamePlayer player) {
    }

    @Override
    default void onPlayerRespawned(@Nonnull GamePlayer player) {
    }

    @Override
    default void onDeath(@Nonnull GamePlayer player) {
    }

}
