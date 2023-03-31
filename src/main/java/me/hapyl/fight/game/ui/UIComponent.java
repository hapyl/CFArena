package me.hapyl.fight.game.ui;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Indicates that this component can return additional information to be displayed in the player's UI.
 */
public interface UIComponent {

    @Nonnull
    String getString(Player player);

}
