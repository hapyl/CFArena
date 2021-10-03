package kz.hapyl.fight.game;

import org.bukkit.entity.Player;

// FIXME: 003. 10/03/2021 - Fix this class maybe using enums or idk what the fuck if this
public class Stat {

	private final Player player;

	private long coins;
	private long kills;
	private long deaths;

	public Stat(Player player) {
		this.player = player;
	}

	public long getCoins() {
		return coins;
	}

	public String getCoinsString() {
		return coins > 0 ? " &7You've earned &e%s &7coins this game!".formatted(coins) : " &7You haven't earned any coins this game.";
	}

	public String getKillsString() {
		return kills > 0 ? " &7You've killed &l%s &7opponents this game!".formatted(kills) : " &7You haven't killed anyone this game.";
	}

	public String getDeathsString() {
		return deaths > 0 ? " &7You've died &l%s &7times this game!".formatted(deaths) : " &7You haven't died this game. Wow.";
	}

	public void setCoins(long coins) {
		this.coins = coins;
	}

	public long getKills() {
		return kills;
	}

	public void setKills(long kills) {
		this.kills = kills;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public void addCoins(long coins) {
		setCoins(getCoins() + coins);
	}

	public void addDeaths(long deaths) {
		setDeaths(getDeaths() + deaths);
	}

	public void addKills(long kills) {
		setKills(getKills() + kills);
	}

	public Player getPlayer() {
		return player;
	}


}
