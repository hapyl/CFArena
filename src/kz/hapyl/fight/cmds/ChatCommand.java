package kz.hapyl.fight.cmds;

import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatCommand extends SimplePlayerAdminCommand {

	public ChatCommand(String str) {
		super(str);
		this.setUsage("chat [boolean]");
	}

	@Override
	protected void execute(Player player, String[] strings) {

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}