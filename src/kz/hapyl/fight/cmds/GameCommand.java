package kz.hapyl.fight.cmds;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class GameCommand extends SimplePlayerAdminCommand {

	public GameCommand(String str) {
		super(str);
		this.setUsage("game (Start/Stop)");
		this.setDescription("Allows admins to control the game instance.");
	}

	@Override
	protected void execute(Player player, String[] args) {
		// game (start/stop/pause)
		if (args.length == 1) {
			final Manager manager = Manager.current();

			switch (args[0].toLowerCase(Locale.ROOT)) {
				case "start" -> {
					if (manager.isGameInProgress()) {
						Chat.sendMessage(player, "&cA game is already in progress, stop it first!");
						return;
					}

					Chat.sendMessage(player, "&aCreating new game instance...");
					manager.createNewGameInstance();

				}

				case "stop" -> {
					if (!manager.isGameInProgress()) {
						Chat.sendMessage(player, "&cCouldn't find any game instances in progress.");
						return;
					}

					manager.stopCurrentGame();
				}

				default -> {
					Chat.sendMessage(player, "&cInvalid argument! " + this.getUsage());
				}
			}

			return;
		}
		Chat.sendMessage(player, "&cNot enough arguments! " + this.getUsage());
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}