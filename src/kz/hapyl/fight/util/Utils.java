package kz.hapyl.fight.util;

import org.bukkit.ChatColor;

public class Utils {

	// TODO: 030. 08/30/2021
	public static String colorString(String str, String defColor) {
		final StringBuilder builder = new StringBuilder();
		final String[] strings = str.split(" ");
		for (final String string : strings) {
			if (string.endsWith("%")) {
				builder.append(ChatColor.RED);
			}
			else if (string.endsWith("s") && string.contains("[0-9]")) {
				builder.append(ChatColor.AQUA);
			}
			else {
				builder.append(defColor);
			}
			builder.append(string).append(" ");
		}
		return builder.toString().trim();
	}

	public static void main(String[] args) {
		System.out.println(colorString("Increased damage by 10% fo 30s.", "&7"));
	}

}
