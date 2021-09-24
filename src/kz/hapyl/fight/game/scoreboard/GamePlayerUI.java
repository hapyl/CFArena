package kz.hapyl.fight.game.scoreboard;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.effect.GameEffect;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.task.ShutdownAction;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

// this controls scoreboard and tab-list
public class GamePlayerUI {

	private final Player player;
	private final Scoreboarder builder;

	public GamePlayerUI(Player player) {
		this.player = player;
		this.builder = new Scoreboarder("&e&lCLASSES FIGHT &cArena");
		this.updateScoreboard();
		Main.getPlugin().getScoreList().register(this);

		new GameTask() {
			@Override
			public void run() {
				if (player == null || !player.isOnline()) {
					this.cancel();
					return;
				}

				player.setPlayerListHeaderFooter(Chat.format(formatHeaderFooter()[0]), Chat.format(formatHeaderFooter()[1]));
				player.setPlayerListName(formatPlayerListName());

				// update scoreboard
				updateScoreboard();

			}
		}.runTaskTimer(0, 20).setShutdownAction(ShutdownAction.IGNORE);

	}

	private String[] formatHeaderFooter() {

		// effects
		final StringBuilder footer = new StringBuilder();
		footer.append("\n");

		footer.append("&e&lPing: &f").append(player.getPing());

		// Display effects if game in progress
		if (Manager.current().isGameInProgress()) {
			footer.append("\n&e&lActive Effects:\n");
			final GamePlayer gp = GamePlayer.getPlayer(this.player);
			if (gp == null || gp.getActiveEffects().isEmpty()) {
				footer.append("&8None!");
			}
			else {
				// {Positive}{Name} - {Time}
				gp.getActiveEffects().forEach((type, active) -> {
					final GameEffect gameEffect = type.getGameEffect();
					footer.append(gameEffect.isPositive() ? "&a" : "&c");
					footer.append(gameEffect.getName());
					footer.append(" &f- ");
					footer.append(new SimpleDateFormat("mm:ss").format(active.getRemainingTicks() * 50));
				});
			}
		}
		footer.append("\n");

		return new String[]{"\n&e&lCLASSES FIGHT\n&cArena\n\n&fTotal Players: &l" + Bukkit.getOnlinePlayers().size(), footer.toString()};
	}

	private String formatPlayerListName() {
		final StringBuilder builder = new StringBuilder();
		final Heroes hero = Manager.current().getSelectedHero(player);

		builder.append("&6&l").append(hero.getHero().getName()).append(" ");
		builder.append(player.isOp() ? "&c🛡 " : "&e").append(player.getName());

		if (Manager.current().isGameInProgress()) {
			builder.append(" &0| ");
			final GamePlayer gamePlayer = GamePlayer.getPlayer(this.player);
			if (gamePlayer != null) {
				if (gamePlayer.isSpectator()) {
					builder.append("&7&lSpectator");
				}
				else if (gamePlayer.isDead()) {
					builder.append("&4☠☠☠");
				}
				else {
					final boolean usingUltimate = hero.getHero().isUsingUltimate(player);
					if (usingUltimate) {
						builder.append("&b&lIN USE");
					}
					else if (gamePlayer.isUltimateReady()) {
						builder.append("&b&lREADY");
					}
					else {
						builder.append("&b%s/%s &l※".formatted(gamePlayer.getUltPoints(), gamePlayer.getUltPointsNeeded()));
					}
				}
			}
		}

		return Chat.format(builder.toString());
	}

	public void updateScoreboard() {

		final Database database = Database.getDatabase(player);
		this.builder.setLines("",
				"Welcome %s to the".formatted(player.getName()),
				"&lClasses Fight &fArena!",
				"",
				" &e&lCoins: &f%s".formatted(database.getCurrency().getCoins()),
				" &e&lSelected Class: &f%s".formatted(Manager.current().getSelectedHero(player).getHero().getName()),
				"",
				"&bTest version, report any",
				"&bbugs you'll find!"
		);
		this.builder.addPlayer(player);

	}

	public Player getPlayer() {
		return player;
	}
}
