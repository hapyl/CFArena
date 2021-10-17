package kz.hapyl.fight.game.scoreboard;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.AbstractGameInstance;
import kz.hapyl.fight.game.AbstractGamePlayer;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.effect.GameEffect;
import kz.hapyl.fight.game.gamemode.Modes;
import kz.hapyl.fight.game.gamemode.modes.Deathmatch;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.task.ShutdownAction;
import kz.hapyl.spigotutils.SpigotUtilsPlugin;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.IntInt;
import kz.hapyl.spigotutils.module.player.song.Song;
import kz.hapyl.spigotutils.module.player.song.SongPlayer;
import kz.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.TreeMap;

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

		// Display NBS player if playing a song
		final SongPlayer songPlayer = SpigotUtilsPlugin.getPlugin().getSongPlayer();
		if (songPlayer.getCurrentSong() != null) {
			final Song song = songPlayer.getCurrentSong();
			final StringBuilder builder = new StringBuilder();
			final int frame = (int)(songPlayer.getCurrentFrame() * 30 / songPlayer.getMaxFrame());

			for (int i = 0; i < 30; i++) {
				builder.append(i < frame ? ChatColor.DARK_AQUA : ChatColor.DARK_GRAY);
				builder.append("|");
			}

			footer.append("\n\n&e&lSong Player:\n");
			footer.append("&f%s - %s\n&8%s".formatted(
					song.getOriginalAuthor(),
					song.getName(),
					songPlayer.isPaused() ? "&e&lPAUSE" : builder.toString()
			));
		}

		// Display effects if game in progress
		if (Manager.current().isGameInProgress()) {
			footer.append("\n\n&e&lActive Effects:\n");
			final GamePlayer gp = GamePlayer.getAlivePlayer(this.player);
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
			final GamePlayer gamePlayer = GamePlayer.getAlivePlayer(this.player);
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
		final Manager current = Manager.current();

		this.builder.getLines().clear();
		this.builder.addLines(
				"",
				"Welcome %s to the".formatted(this.player.getName()),
				"&lClasses Fight &fArena!",
				""
		);

		if (current.isGameInProgress()) {
			final AbstractGameInstance game = current.getCurrentGame();
			final AbstractGamePlayer gamePlayer = GamePlayer.getPlayer(this.player);
			this.builder.addLines(
					"&6&lGame: &8" + game.hexCode(),
					" &e&lMap: &f%s".formatted(current.getCurrentMap().getMap().getName()),
					" &e&lTime Left: &f%s".formatted(new SimpleDateFormat("mm:ss").format(game.getTimeLeftRaw())),
					" &e&lStatus: &f%s".formatted(gamePlayer.getStatusString())
			);

			if (game.getCurrentMode() == Modes.DEATH_MATCH) {

				final TreeMap<Long, GamePlayer> top3 = ((Deathmatch)game.getMode()).getTopKills(game, 3);
				this.builder.addLines(
						"",
						"&6&lDeathmatch:"
				);

				final IntInt i = new IntInt(1);
				top3.forEach((val, pla) -> {
					if (val == 0) {
						return;
					}
					builder.addLines(" &e&l#%s &f%s &l🗡%s".formatted(i.get(), pla.getPlayer().getName(), val));
					i.increment();
				});

				for (int j = i.get(); j <= 3; j++) {
					builder.addLines(" &e...");
				}

			}

		}
		else {
			this.builder.addLines(
					"&6&lLobby:",
					" &e&lMap: &f%s".formatted(current.getCurrentMap().getMap().getName()),
					" &e&lMode: &f%s".formatted(current.getCurrentMode().getMode().getName()),
					" &e&lCoins: &f%s".formatted(database.getCurrency().getCoinsString()),
					" &e&lHero: &f%s".formatted(current.getSelectedHero(player).getHero().getName())
			);
		}

		this.builder.addLine("");
		this.builder.updateLines();
		this.builder.addPlayer(player);

	}

	public Player getPlayer() {
		return player;
	}
}
