package me.hapyl.fight.cmds;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ReportCommandCommand extends SimplePlayerAdminCommand {

	public ReportCommandCommand(String str) {
		super(str);
		this.setUsage("report " + Arrays.toString(Type.values()));
	}

	@Override
	protected void execute(Player player, String[] args) {
		if (args.length == 1) {

			final Type type = Validate.getEnumValue(Type.class, args[0]);
			if (type == null) {
				Chat.sendMessage(player, "&cInvalid type! " + this.getUsage());
				return;
			}

			switch (type) {

				case GAME -> {
					final GameInstance gameInstance = Manager.current().getGameInstance();
					if (gameInstance == null) {
						sendReportMessage(player, "&cThere is no active game instance!");
						return;
					}

					sendReportMessage(player, "&a&lGame Instance @" + gameInstance.hashCode() + ":");

					if (gameInstance.isTimeIsUp()) {
						sendReportMessage(player, " &aFinished!");
					}
					else {
						sendReportMessage(player, " &aIn Progress! " + BukkitUtils.roundTick((int)gameInstance.getTimeLeft()) + "s left.");
					}

					sendReportMessage(player, "");
					sendReportMessage(player, "&a&lPlayers:");

					System.out.println(gameInstance.getPlayers());
					gameInstance.getPlayers().forEach((uuid, gp) -> {
						sendReportMessage(player, " %s (%s) &f[%s&f]".formatted(gp.getPlayer().getName(),
								gp.getHero().getName(),
								gp.isDead() ? "&c&lDEAD" : gp.isSpectator() ? "&7&lSPECTATOR" : "&a&lALIVE &a(%s)".formatted(gp.getHealth())));
					});

				}

			}

			return;
		}
		Chat.sendMessage(player, "&cNot enough arguments! " + this.getUsage());
	}

	private void sendReportMessage(Player player, String message, Object... toReplace) {
		Chat.sendMessage(player, "&6&lREPORT&e: &f" + message, toReplace);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.completerSort(super.arrayToList(Type.values()), args);
	}

	private enum Type {
		GAME,
		PLAYER
	}

}