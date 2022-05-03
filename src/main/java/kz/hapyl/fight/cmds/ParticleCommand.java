package kz.hapyl.fight.cmds;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ParticleCommand extends SimplePlayerAdminCommand {
	public ParticleCommand(String name) {
		super(name);
		this.setUsage("part (Particle) [amount] [offsetX] [offsetY] [offsetZ] [speed]");
	}

	@Override
	protected void execute(Player player, String[] args) {
		if (args.length < 1) {
			this.sendInvalidUsageMessage(player);
			return;
		}

		final Particle particle = Validate.getEnumValue(Particle.class, args[0]);

		if (particle == null) {
			Chat.sendMessage(player, "&cNot sure there is particle called \"%s\".".formatted(args[0]));
			return;
		}

		final int amount = Validate.getInt(args.length >= 2 ? args[1] : 1);
		final double offsetX = Validate.getDouble(args.length >= 3 ? args[2] : 0.0d);
		final double offsetY = Validate.getDouble(args.length >= 4 ? args[3] : 0.0d);
		final double offsetZ = Validate.getDouble(args.length >= 5 ? args[4] : 0.0d);
		final float speed = Validate.getFloat(args.length >= 6 ? args[5] : 0.0d);

		PlayerLib.spawnParticle(player.getEyeLocation().add(0.0d, 0.5d, 0.0d), particle, amount, offsetX, offsetY, offsetZ, speed);
		final StringBuilder builder = new StringBuilder("&aSpawned x%s &l%s &a[%s, %s, %s]".formatted(amount, Chat.capitalize(particle), offsetX, offsetY, offsetZ));

		// Spawned x5 SPIT ([0.0d, 0.0d, 0.0d]) [with speed 1.0] above your head.
		if (speed > 0.0d) {
			builder.append(" with speed &l").append(speed);
		}

		builder.append(" &aabove your head!");
		Chat.sendMessage(player, builder.toString());

	}

	@Override
	protected List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return completerSort(arrayToList(Particle.values()), args);
		}
		return null;
	}
}
