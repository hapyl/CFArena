package me.hapyl.fight.game.tutorial;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ChatTutorial implements Tutorial {

	// TODO: 019. 10/19/2021 -> redo using book
	private final Map<PageType, Page> pages;

	public ChatTutorial() {
		this.pages = new HashMap<>();

		// About classes
		this.pages.put(PageType.GENERAL, new Page()
				.add("&7Welcome, {player}, to the &6Classes Fight &cArena&7!")
				.add()
				.add());

	}

	public Map<PageType, Page> getPages() {
		return pages;
	}

	@Nullable
	public Page getPage(PageType type) {
		return pages.get(type);
	}

	@Override
	public void display(Player player) {
		Chat.sendMessage(player, "");
		Chat.sendMessage(player, "&7Welcome, %s, to the &6Classes Fight &cArena&7!", player.getName());
		Chat.sendMessage(player, "");
		Chat.sendMessage(player, " &7Use &e/hero &7to select a hero to play as.");
		Chat.sendMessage(player, " &7Use &e/team &7to change a team.");
		Chat.sendMessage(player, " &7Use &e/map &7to select a map to play on.");
		Chat.sendMessage(player, " &7Use &e/mode &7to select a game mode.");
		Chat.sendMessage(player, "");
		PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
	}


}
