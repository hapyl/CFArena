package kz.hapyl.fight.cmds;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UltimateCommand extends SimplePlayerAdminCommand {

	public UltimateCommand(String str) {
		super(str);
	}

	@Override
	protected void execute(Player player, String[] strings) {
		if (Manager.current().isGameInProgress()) {
			final GamePlayer gp = Manager.current().getGameInstance().getPlayer(player);
			gp.setUltPoints(gp.getUltPointsNeeded());
			player.sendMessage("charged your ultimate!");
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}