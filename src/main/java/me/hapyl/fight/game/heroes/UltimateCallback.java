package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * The callback is used to execute code <b>after</b> the ultimate casting is done.
 * The implementation of this can be seen in <code>{@link me.hapyl.fight.game.heroes.archive.pytaria.Pytaria#useUltimate(GamePlayer)}</code>
 */
public interface UltimateCallback {

    /**
     * A more user-friendly way to annotate that ultimate is fine, rather than using <code>null</code> directly.
     *
     * <pre>
     *     UltimateCallback useUltimate(GamePlayer player) {
     *         player.makeSuperCool();
     *         player.sendMessage("You are super cool!");
     *
     *         return UltimateCallback.OK;
     *     }
     *
     *     ⬆ Looks better than ⬇
     *
     *     UltimateCallback useUltimate(GamePlayer player) {
     *         player.makeSuperCool();
     *         player.sendMessage("You are super cool!");
     *
     *         return null;
     *     }
     * </pre>
     */
    UltimateCallback OK = null;

    /**
     * Executes after the casting of ultimate is done.
     *
     * @param player - Player.
     */
    void callback(@Nonnull GamePlayer player);

}
