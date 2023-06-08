package me.hapyl.fight.game.talents;

import org.bukkit.entity.Player;

public interface Removable {

    void remove();

    /**
     * Called whenever this value is removed because the new value with the same type is created.
     *
     * @param player - Player.
     */
    default void onReplace(Player player) {
    }

}
