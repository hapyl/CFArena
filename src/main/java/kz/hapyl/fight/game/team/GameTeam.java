package kz.hapyl.fight.game.team;

import kz.hapyl.fight.game.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameTeam {

	private final List<GamePlayer> players;
	private ChatColor color = ChatColor.YELLOW;

	public GameTeam() {
		players = new ArrayList<>();
	}

	public void setColor(ChatColor color) {
		this.color = color;
	}

	public ChatColor getColor() {
		return color;
	}

	public boolean isTeamMember(GamePlayer player) {
		return players.contains(player);
	}

	public boolean isTeamMember(Player player) {
		for (final GamePlayer gp : players) {
			if (gp.compare(player)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAlive() {
		for (final GamePlayer player : players) {
			if (player.isAlive()) {
				return true;
			}
		}
		return false;
	}

	public boolean isDead() {
		return !isAlive();
	}

	public List<GamePlayer> getPlayers() {
		return players;
	}
}
