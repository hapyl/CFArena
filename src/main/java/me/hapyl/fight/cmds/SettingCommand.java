package me.hapyl.fight.cmds;

import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingCommand extends SimplePlayerCommand {

	public SettingCommand(String str) {
		super(str);
		this.setUsage("setting [Setting] (Value)");
	}

	@Override
	protected void execute(Player player, String[] args) {
		if (args.length == 0) {
			new SettingsGUI(player);
		}
		else {

			final Setting setting = Validate.getEnumValue(Setting.class, args[0]);
			if (setting == null) {
				Chat.sendMessage(player, "&cInvalid setting \"%s\"!", args[0]);
				return;
			}

			final boolean value = args.length >= 2 ? args[1].equalsIgnoreCase("true") : !setting.isEnabled(player);
			setting.setEnabled(player, value);

		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.completerSort(Setting.values(), args);
	}

}