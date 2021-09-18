package kz.hapyl.fight.game;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;

public class ChatController implements Listener {

	private boolean chatStatus = true;
	private final String format = "&4&l%s &6%s %s%s: &f%s";
	private final Map<UUID, Long> muteDuration = Maps.newHashMap();

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

		Bukkit.getOnlinePlayers().forEach(online -> {
			formatAndSendMessage(player, message, online);
		});


	}

	private void formatAndSendMessage(Player sender, String message, Player receiver) {
		// [Dead/Spec] [Class] [Color](Name): Message
		final StringBuilder builder = new StringBuilder();

		if (Manager.current().isGameInProgress()) {
			final GamePlayer player = GamePlayer.getPlayer(sender);
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
		if (message.contains(atReceiverName)) {
			message = message.replace(atReceiverName, ChatColor.YELLOW + atReceiverName + ChatColor.WHITE);
			PlayerLib.playSound(receiver, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
		}

		builder.append(message);
		Chat.sendMessage(receiver, builder.toString());

	}

	public boolean isMuted(Player player) {
		// todo
		return false;
	}

}
