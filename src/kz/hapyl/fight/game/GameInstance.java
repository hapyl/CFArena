package kz.hapyl.fight.game;

import com.google.common.collect.Maps;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.ui.UIComponent;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

public class GameInstance implements GameElement {

	private final String hexCode;

	private final long startedAt;
	private final long timeLimit;
	private final Map<UUID, GamePlayer> players;

	private final GameTask gameTask;

	public GameInstance(long timeLimitSec) {
		this.startedAt = System.currentTimeMillis();
		this.timeLimit = timeLimitSec * 1000;
		this.players = Maps.newHashMap();
		this.createGamePlayers();

		this.hexCode = generateHexCode();

		// This is a main ticker of the game.
		this.gameTask = new GameTask() {

			int tick = 0;

			@Override
			public void run() {

				// Auto-Points
				if (tick % 20 == 0) {
					players.values().forEach(player -> {
						player.addUltimatePoints(1);
					});
				}

				// Game UI
				if (tick % 5 == 0) {
					players.values().forEach(gp -> {
						if (gp.isAlive() && !gp.isSpectator()) {
							final StringBuilder builder = new StringBuilder("&c%s &c❤ &0| &b%s &l※".formatted(BukkitUtils.decimalFormat(gp.getHealth()), getUltimateString(gp)));
							final Player player = gp.getPlayer();

							if (gp.getHero() instanceof UIComponent uiHero) {
								builder.append(" &0| ").append(uiHero.getString(player));
							}

							Chat.sendActionbar(player, builder.toString());
						}
					});
				}

				++tick;

			}
		}.runTaskTimer(0, 1);

	}

	private String getUltimateString(GamePlayer gp) {
		final Player player = gp.getPlayer();
		final UltimateTalent ultimate = gp.getUltimate();
		final String pointsString = "%s/%s".formatted(gp.getUltPoints(), gp.getUltPointsNeeded());

		if (ultimate.hasCd(player)) {
			return "&7%s &b(%ss)".formatted(pointsString, BukkitUtils.roundTick(ultimate.getCdTimeLeft(player)));
		}

		else if (gp.isUltimateReady()) {
			return "&b&lREADY";
		}

		return pointsString;
	}

	public long getTimeLeft() {
		return (timeLimit - (System.currentTimeMillis() - startedAt)) / 50;
	}

	public boolean isTimeIsUp() {
		return System.currentTimeMillis() >= startedAt + timeLimit;
	}

	@Nullable
	public GamePlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	@Nullable
	public GamePlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	public Map<UUID, GamePlayer> getPlayers() {
		return players;
	}

	public List<GamePlayer> getAlivePlayers(Heroes heroes) {
		return getAlivePlayers(gp -> gp.getHero() == heroes.getHero());
	}

	public List<GamePlayer> getAlivePlayers() {
		return getAlivePlayers(gp -> gp.getPlayer().isOnline());
	}

	public List<GamePlayer> getAlivePlayers(Predicate<GamePlayer> predicate) {
		final List<GamePlayer> players = new ArrayList<>();
		this.players.forEach((uuid, gp) -> {
			if (gp.isAlive() && predicate.test(gp)) {
				players.add(gp);
			}
		});
		return players;
	}

	private void createGamePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			final Heroes hero = Manager.current().getSelectedHero(player);
			// todo -> impl spectator settings here!
			final GamePlayer gamePlayer = new GamePlayer(player, hero.getHero());
			gamePlayer.updateScoreboard(true);
			players.put(player.getUniqueId(), gamePlayer);
		});
	}

	// TODO: 018. 09/18/2021 -> impl modes
	public void checkWinCondition() {
		int alivePlayers = 0;
		Player potentialWinner = null;
		for (final GamePlayer player : this.players.values()) {
			if (player.isAlive()) {
				++alivePlayers;
				potentialWinner = player.getPlayer();
			}
		}
		if (alivePlayers <= 1) {
			if (potentialWinner == null) {
				Chat.broadcast("&eWin condition met but no winner?");
			}
			Manager.current().countEverything();
			Manager.current().stopCurrentGame();
		}
	}

	@Override
	public void onStart() {
		for (final Heroes value : Heroes.values()) {
			value.getHero().onStart();
		}

		Chat.broadcast("&7&oStarting game instance #%s...", this.hexCode());
	}

	@Override
	public void onStop() {
		this.getPlayers().values().forEach(gp -> {
			final Hero hero = gp.getHero();
			if (hero instanceof PlayerElement heroPE) {
				heroPE.onStop(gp.getPlayer());
			}
		});
		Chat.broadcast("&7&oStopping game instance #%s...".formatted(this.hexCode()));
	}

	public String hexCode() {
		return this.hexCode;
	}

	private String generateHexCode() {
		return Integer.toHexString(new Random().nextInt());
		//final StringBuilder builder = new StringBuilder();
		//for (int i = 0; i < 8; i++) {
		//	builder.append(Integer.toHexString(new Random().nextInt(0x10) + 0x10));
		//}
		//return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final GameInstance that = (GameInstance)o;
		return startedAt == that.startedAt && timeLimit == that.timeLimit && Objects.equals(players, that.players);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startedAt, timeLimit, players);
	}
}
