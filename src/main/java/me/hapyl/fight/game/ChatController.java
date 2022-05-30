package me.hapyl.fight.game;

import me.hapyl.fight.game.setting.Setting;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class ChatController implements Listener {

	private final boolean chatStatus = true;
	private final String format = "&4&l%s &6%s %s%s: &f%s";
	private final Map<Player, String> lastMessage = new HashMap<>();

	/**
	 * Mojang can't make this actually async...
	 */
	@EventHandler()
	public void handleChat(AsyncPlayerChatEvent ev) {
		final Player player = ev.getPlayer();
		final String message = ev.getMessage();

		ev.setCancelled(true); // don't remove

		if (isMuted(player)) {
			Chat.sendMessage(player, "&cYou're currently muted!");
			return;
		}

		if (!chatStatus && !player.isOp()) {
			Chat.sendMessage(player, "&cChat is currently disabled!");
			return;
		}

		// Pre-Checks
		if (isSameMessageAsLast(player, message) && !player.isOp()) {
			Chat.sendMessage(player, "&cYou cannot send the same message twice.");
			return;
		}

		lastMessage.put(player, message);

		Bukkit.getOnlinePlayers().forEach(online -> {
			formatAndSendMessage(player, message, online);
		});

	}

	private void formatAndSendMessage(Player sender, String message, Player receiver) {
		// [Dead/Spec] [Class] [Color](Name): Message
		final StringBuilder builder = new StringBuilder();

		if (Manager.current().isGameInProgress()) {
			final GamePlayer player = GamePlayer.getAlivePlayer(sender);
			// nullable player if game does not exist
			if (player != null) {
				if (player.isDead()) {
					builder.append("&4☠☠☠ ");
				}
				if (player.isSpectator()) {
					builder.append("&7&lSpectator ");
				}
			}
		}

		builder.append(ChatColor.GOLD).append(Manager.current().getSelectedHero(sender).getHero().getName()).append(" ");

		builder.append(sender.isOp() ? ChatColor.RED : ChatColor.YELLOW);
		builder.append(sender.getName()).append("&f: ");

		// tag receiver
		final String atReceiverName = "@" + receiver.getName();
		if (message.contains(atReceiverName) && Setting.CHAT_PING.isEnabled(receiver)) {
			message = message.replace(atReceiverName, "&e%s&f".formatted(atReceiverName));
			PlayerLib.playSound(receiver, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
		}

		message = Chat.format(message);
		if (!sender.isOp()) {
			message = ChatColor.stripColor(message);
		}

		builder.append(message);
		Chat.sendMessage(receiver, builder.toString());

	}

	public boolean isSameMessageAsLast(Player player, String string) {
		return lastMessage.containsKey(player) && lastMessage.get(player).contains(string);
	}

	/**
	 * Using scoreboard tags for now.
	 */
	public boolean isMuted(Player player) {
		return player.getScoreboardTags().contains("Muted");
	}

	public void setMuted(Player player, boolean flag) {
		if (flag) {
			player.addScoreboardTag("Muted");
		}
		else {
			player.removeScoreboardTag("Muted");
		}
	}

}
