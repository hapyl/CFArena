package me.hapyl.fight.cmds;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.gui.MapSelectGUI;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MapCommand extends SimplePlayerCommand {

	public MapCommand(String str) {
		super(str);
		this.setUsage("map [Map]");
	}

	@Override
	protected void execute(Player player, String[] args) {
		// Map [MapName]
		if (args.length >= 1) {

			final GameMaps value = Validate.getEnumValue(GameMaps.class, args[0]);
			if (value == null) {
				PlayerLib.villagerNo(player, "&cInvalid map!");
				return;
			}

			Manager.current().setCurrentMap(value, player);
			return;
		}

		MapSelectGUI.openGUI(player);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return completerSort(Utils.collectionToStringList(GameMaps.getPlayableMaps(), GameMaps::name), args);
	}

}