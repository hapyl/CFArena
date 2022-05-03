package kz.hapyl.fight.game.scoreboard;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreList {

	public final ConcurrentHashMap<UUID, GamePlayerUI> perPlayer;

	public ScoreList() {
		this.perPlayer = new ConcurrentHashMap<>();
	}

	public void register(GamePlayerUI score) {
		this.perPlayer.put(score.getPlayer().getUniqueId(), score);
	}

	public GamePlayerUI getScore(Player player) {
		return this.perPlayer.getOrDefault(player.getUniqueId(), null);
	}

}
