package me.hapyl.fight.game.element;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface StrictElementHandler {

    /**
     * Called <b>once</b> whenever a {@link GameInstance} initially starts.
     *
     * @param instance - Game instance.
     */
    void onStart(@Nonnull GameInstance instance);

    /**
     * Called <b>once</b> whenever a {@link GameInstance} stops.
     *
     * @param instance - Game instance.
     */
    void onStop(@Nonnull GameInstance instance);

    /**
     * Called <b>once</b> whenever {@link GamePlayer}s are revealed in a {@link GameInstance}.
     *
     * @param instance - Game instance.
     */
    void onPlayersRevealed(@Nonnull GameInstance instance);

}
