package kz.hapyl.fight.util;

import org.bukkit.Bukkit;

public final class debug {

	public static void sout(Object obj) {
		System.out.println(obj);
		Bukkit.broadcastMessage(obj == null ? "null" : obj.toString());
	}

}
