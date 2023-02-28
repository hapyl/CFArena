package me.hapyl.fight.game.ui;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface UIComponent {

	@Nonnull
	String getString(Player player);

}
