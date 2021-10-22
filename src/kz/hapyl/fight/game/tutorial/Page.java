package kz.hapyl.fight.game.tutorial;

import kz.hapyl.spigotutils.module.chat.CenterChat;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Page {

	private final List<String> items;

	public Page() {
		this.items = new ArrayList<>();
	}

	public Page add() {
		return add("");
	}

	public Page add(String str) {
		this.items.add(str);
		return this;
	}

	public Page addCenter(String str) {
		this.items.add(CenterChat.makeString(str));
		return this;
	}

	public List<String> getItems() {
		return items;
	}

	public void sendMessage(Player player) {
		for (String item : items) {
			if (item.contains("{player")) {
				item = item.replace("{player}", player.getName());
			}
			Chat.sendMessage(player, item);
		}
	}

}
