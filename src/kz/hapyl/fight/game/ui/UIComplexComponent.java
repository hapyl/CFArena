package kz.hapyl.fight.game.ui;

import org.bukkit.entity.Player;

public interface UIComplexComponent extends UIComponent {

	String[] getStrings(Player player);

	@Override
	default String getString(Player player) {
		final String[] strings = getStrings(player);
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			builder.append(strings[i]);
			if (i < (strings.length - 1)) {
				builder.append(" &0|&r ");
			}
		}
		return builder.toString();
	}
}
