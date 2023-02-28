package me.hapyl.fight.game.ui;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface UIComplexComponent extends UIComponent {

	String[] getStrings(Player player);

	@Override
	default @Nonnull String getString(Player player) {
		final String[] strings = getStrings(player);
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			builder.append(strings[i]);
			if (i < (strings.length - 1)) {
				builder.append(" %s ".formatted(UIFormat.DIV));
			}
		}
		return builder.toString();
	}
}
