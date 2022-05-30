package me.hapyl.fight.util;

import org.bukkit.Bukkit;

public final class debug {

	public static void sout(Object obj, Object... r) {
		System.out.println(obj);
		Bukkit.broadcastMessage(obj == null ? "null" : obj.toString().formatted(r));
	}

}
