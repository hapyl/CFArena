package me.hapyl.fight.util.collection.automap;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface MapElement {

    void onDeath(@Nonnull GamePlayer player);

    default void onStop(@Nonnull GamePlayer player) {
    }

    void onStop();

}
