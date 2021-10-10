package kz.hapyl.fight.cmds;

import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class AdminCommand extends SimplePlayerAdminCommand {

	public AdminCommand(String str) {
		super(str);
		this.setUsage("classesfightadmin");
		this.setAliases("cfa");
	}

	@Override
	protected void execute(Player player, String[] args) {
		// Damage
		// todo -> automate this
		if (args.length >= 1) {

			final String value = args[0].toLowerCase(Locale.ROOT);
			if (value.equalsIgnoreCase("damage")) {
				double damage = Validate.getDouble(args[1]);
				if (damage > 0.0d) {
					Chat.sendMessage(player, "&aDealt %s damage to you.", damage);
					GamePlayer.damageEntity(player, damage, player, EnumDamageCause.ENTITY_ATTACK);
				}
			}

		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}