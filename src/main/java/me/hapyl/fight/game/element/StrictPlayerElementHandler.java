package me.hapyl.fight.game.element;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface StrictPlayerElementHandler {

    /**
     * Called <b>every time</b> {@link GamePlayer} appears in a {@link GameInstance}, be it when they initially spawn or respawned.
     *
     * @param player - Player.
     */
    void onStart(@Nonnull GamePlayer player);

    /**
     * Called <b>once</b> at the end of a {@link GameInstance}.
     *
     * @param player - Player.
     */
    void onStop(@Nonnull GamePlayer player);

    /**
     * Called <b>once</b> whenever {@link GamePlayer}s are revealed.
     *
     * @param player - Player.
     */
    void onPlayersRevealed(@Nonnull GamePlayer player);

    /**
     * Called <b>every time</b> a {@link GamePlayer} dies.
     * <br>
     * This is not called if death event is cancelled.
     *
     * @param player - Player.
     */
    void onDeath(@Nonnull GamePlayer player);

    /**
     * Called <b>every time</b> a {@link GamePlayer} respawns in a {@link GameInstance}
     *
     * @param player - Player.
     */
    void onRespawn(@Nonnull GamePlayer player);


}
