package me.hapyl.fight.cmds;

import me.hapyl.fight.game.maps.features.Booster;
import me.hapyl.fight.game.maps.features.MathBooster;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class DebugBooster extends SimplePlayerAdminCommand {
	public DebugBooster(String name) {
		super(name);
		this.setUsage("debugBooster (vecX) (vecY) (vecZ)");
	}

	@Override
	protected void execute(Player player, String[] args) {

		if (args.length == 6) {
			final double[] values = new double[6];
			for (int i = 0; i < args.length; i++) {
				values[i] = NumberConversions.toDouble(args[i]) + 0.5d;
			}

			new MathBooster(
					values[0], values[1], values[2],
					values[3], values[4], values[5]
			).launch();
			return;
		}

		if (args.length == 3) {

			final double[] vectors = getVectors(args);
			final BlockLocation location = new BlockLocation(player.getLocation());
			final Vector vector = new Vector(vectors[0], vectors[1], vectors[2]);
			final Booster booster = new Booster(location, vector);

			booster.launch(true);

			final String string = "%s, %s, %s, %s, %s, %s".formatted(
					location.getX(),
					location.getY(),
					location.getZ(),
					vector.getX(),
					vector.getY(),
					vector.getZ()
			);

			final BaseComponent[] baseComponents = new ComponentBuilder(ChatColor.GREEN + "Launching piggy with vector " + vector)
					.event(new ClickEvent(
							ClickEvent.Action.SUGGEST_COMMAND,
							string
					)).create();

			player.spigot().sendMessage(ChatMessageType.CHAT, baseComponents);

			return;
		}
		sendInvalidUsageMessage(player);
	}

	private double[] getVectors(String[] str) {
		if (str.length < 3) {
			return new double[]{0.0d, 0.0d, 0.0d};
		}
		return new double[]{NumberConversions.toDouble(str[0]), NumberConversions.toDouble(str[1]), NumberConversions.toDouble(str[2])};
	}

}
