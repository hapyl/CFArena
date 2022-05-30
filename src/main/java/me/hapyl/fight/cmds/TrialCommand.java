package me.hapyl.fight.cmds;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrialCommand extends SimplePlayerAdminCommand {

	public TrialCommand(String str) {
		super(str);
		setUsage("trial (Hero)");
		setDescription("Starts or ends a trial challenge.");
	}

	@Override
	protected void execute(Player player, String[] strings) {
		final Manager manager = Manager.current();
		final Heroes hero = strings.length >= 1 ? Validate.getEnumValue(Heroes.class, strings[0]) : Heroes.ARCHER;

		if (hero == null) {
			Chat.sendMessage(player, "&cInvalid hero \"%s\"!", strings[0]);
			return;
		}

		if (manager.hasTrial()) {
			final Trial trial = manager.getTrial();
			if (trial.getPlayer() == player) {
				manager.stopTrial();
				return;
			}

			Chat.sendMessage(player, "&cA trial is already in progress. In beta, only one player can use the trial feature.");
			return;
		}

		manager.startTrial(player, hero);
		Chat.sendMessage(player, "&aStarting %s's Hero trial challenge.", hero.getHero().getName());

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.completerSort(Heroes.playableStings(), args);
	}



}