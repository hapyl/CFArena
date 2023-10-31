package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * Indicates that this component can return additional information to be displayed in the player's UI.
 */
public interface UIComponent {

    @Nonnull
    String getString(@Nonnull GamePlayer player);

}
