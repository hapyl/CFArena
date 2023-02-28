package me.hapyl.fight.game.talents.storage.extra;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.Player;

public interface GrimoireTalent {

	Response ERROR = new Response(null, Response.Type.ERROR);

	double[] getValues();

	int getGrimoireCd();

	default String formatValues() {
		return formatValues("");
	}

	default String formatValues(String suffix) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getValues().length; i++) {
			final double value = getValues()[i];
			builder.append("&b").append(value).append(suffix);

			if (i < getValues().length - 1) {
				builder.append("&7/");
			}
		}
		return builder.toString().trim();
	}

	default double getCurrentValue(int level) {
		return getValues()[Numbers.clamp(level, 0, 3)];
	}

	default double getCurrentValue(Player player) {
		return getCurrentValue(HeroHandle.LIBRARIAN.getGrimoireLevel(player));
	}

}
