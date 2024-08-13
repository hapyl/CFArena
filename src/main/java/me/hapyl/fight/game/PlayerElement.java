package me.hapyl.fight.game;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * Indicates that this class a game element that process player actions.
 * <p>
 * Man I was 0.5 years old when I made this... -h
 */
public interface PlayerElement {

    /**
     * A caller that must be implemented by {@link GamePlayer}.
     * <br><br>
     * Invoking any {@link PlayerElement} methods outside the {@link GamePlayer} should count as a violation.
     * It is <b>fine</b> however to re-call or call the methods on members, as example:
     * <pre><code>
     *   void onDeath(GamePlayer player) {
     *       this.onStart(player);
     *   }
     *
     *   void onDeath(GamePlayer player) {
     *       member.onDeath(player);
     *       anotherPrivateMember(player);
     *   }
     * </code></pre>
     */
    interface Caller {

        void callOnStart();

        void callOnStop();

        void callOnDeath();

        void callOnPlayersRevealed();

    }

    /**
     * Called once for each player whenever game stars.
     *
     * @param player - Player.
     */
    default void onStart(@Nonnull GamePlayer player) {
    }

    /**
     * Called once for each player whenever the game stops.
     *
     * @param player - Player.
     */
    default void onStop(@Nonnull GamePlayer player) {
    }

    /**
     * Called once for each player whenever they die.
     *
     * @param player - Player.
     */
    default void onDeath(@Nonnull GamePlayer player) {
    }

    /**
     * Called once for each player whenever players are revealed.
     *
     * @param player - Player.
     */
    default void onPlayersRevealed(@Nonnull GamePlayer player) {
    }

}
