package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.InsteadOfNull;

import javax.annotation.Nonnull;

/**
 * Indicates that this component can return additional information to be displayed in the player's UI.
 */
public interface UIComponent {

    /**
     * {@link String} to display in {@link GamePlayer} actionbar.
     *
     * @param player - Player.
     */
    @Nonnull
    @InsteadOfNull("empty string")
    String getString(@Nonnull GamePlayer player);

}
