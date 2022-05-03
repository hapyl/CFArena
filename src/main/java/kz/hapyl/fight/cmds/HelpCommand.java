package kz.hapyl.fight.cmds;

import kz.hapyl.fight.Main;
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SimplePlayerCommand {

	public HelpCommand(String str) {
		super(str);
		this.setUsage("help");
		this.setAliases("commands", "about");
	}

	@Override
	protected void execute(Player player, String[] strings) {
		player.sendMessage("not yet implemented, there is hello screen:");
		Main.getPlugin().getTutorial().display(player);
	}


	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}