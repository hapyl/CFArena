package kz.hapyl.fight.util;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public interface MessageSender {

	void sendMessage(Player player, String string, Object... objects);

	default void broadcastMessage(String string, Object... objects) {
		Chat.broadcast(string, objects);
	}

}
