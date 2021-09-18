package kz.hapyl.fight.game;

import com.google.common.collect.Maps;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GameInstance implements GameElement {

	private final long startedAt;
	private final long timeLimit;
	private final Map<UUID, GamePlayer> players;

	public GameInstance(long timeLimitSec) {
		this.startedAt = System.currentTimeMillis();
		this.timeLimit = timeLimitSec * 1000;
		this.players = Maps.newHashMap();
		this.createGamePlayers();
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

	private void createGamePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			final Heroes hero = Manager.current().getSelectedHero(player);
			// todo -> impl spectator settings here!
			final GamePlayer gamePlayer = new GamePlayer(player, hero.getHero());
			players.put(player.getUniqueId(), gamePlayer);
		});
	}

	public void checkWinCondition() {
		// TODO: 018. 09/18/2021 -> impl modes
		int alivePlayers = 0;
		Player potentialWinner = null;
		for (final GamePlayer player : this.players.values()) {
			if (!player.isDead()) {
				++alivePlayers;
				potentialWinner = player.getPlayer();
			}
		}
		if (alivePlayers <= 1 && potentialWinner != null) {
			Chat.broadcast("last player standing win " + potentialWinner.getName());
			Manager.current().stopCurrentGame();
		}
	}

	@Override
	public void onStart() {
		Bukkit.broadcastMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Started game instance " + this.hashCode());
	}

	@Override
	public void onStop() {
		Bukkit.broadcastMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Stopped game instance " + this.hashCode());
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
