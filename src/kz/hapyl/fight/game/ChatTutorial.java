package kz.hapyl.fight.game;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ChatTutorial implements Tutorial {

	@Override
	public void display(Player player) {
		Chat.sendMessage(player, "");
		Chat.sendMessage(player, "&7Welcome, %s, to the &6Classes Fight &cArena&7!", player.getName());
		Chat.sendMessage(player, "");
		Chat.sendMessage(player, " &7Use &e/hero &7to select a hero to play as.");
		Chat.sendMessage(player, " &7Use &e/map &7to change select a map to play on.");
		Chat.sendMessage(player, "");
		PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
	}
}
