package kz.hapyl.fight.cmds;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.gui.HeroSelectGUI;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HeroCommand extends SimplePlayerCommand {

	public HeroCommand(String str) {
		super(str);
		this.setUsage("hero (Hero)");
		this.setDescription("Allows to select a hero to play as!");
	}

	@Override
	protected void execute(Player player, String[] args) {
		if (Manager.current().isGameInProgress()) {
			Chat.sendMessage(player, "&cUnable to change hero during the game!");
			return;
		}

		if (args.length == 1) {
			final Heroes hero = Validate.getEnumValue(Heroes.class, args[0]);

			if (hero == null) {
				Chat.sendMessage(player, "&cNo such hero as '%s'!", args[0]);
				return;
			}

			Main.getPlugin().getManager().setSelectedHero(player, hero);
			return;
		}

		new HeroSelectGUI(player);

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.completerSort(super.arrayToList(Heroes.values()), args);
	}

}