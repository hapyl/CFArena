package me.hapyl.fight.game.talents;

import me.hapyl.fight.annotate.SelfCallable;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface Removable {
    /**
     * The remove method that must be implemented by the removable.
     * <p>
     *
     * <h1>
     * Plugin must not, and should not call this method within the removable!
     * The method is handled automatically by the {@link CreationTalent}!
     * </h1>
     * <p>
     * Call {@link CreationTalent#removeCreation(Player, Creation)} to properly handle the removal of the object!
     */
    @SelfCallable(false)
    void remove();

    /**
     * Called whenever this value is removed because the new value with the same type is created.
     *
     * @param player - Player.
     */
    default void onReplace(@Nonnull GamePlayer player) {
    }
}
