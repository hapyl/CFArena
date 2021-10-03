package kz.hapyl.fight.game.database.entry;

import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.database.DatabaseEntry;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;

public class CurrencyEntry extends DatabaseEntry {
	public CurrencyEntry(Database database) {
		super(database);
	}

	public long getCoins() {
		return this.getConfig().getLong("currency.coins", 0L);
	}

	public void addCoins(long amount) {
		this.setCoins(this.getCoins() + amount);
	}

	public void removeCoins(long amount) {
		this.setCoins(this.getCoins() - amount);
	}

	public void setCoins(long amount) {
		this.getConfig().set("currency.coins", amount);
	}

	public void awardCoins(Award award) {
		addCoins(award.getAmount());
		Chat.sendMessage(this.getPlayer(), "&6&l+%s Coins &e(%s)", award.getAmount(), award.getReason());
		PlayerLib.playSound(this.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
	}

	public enum Award {

		PLAYER_KILL(100, "Opponent Eliminated."),
		GAME_WON(1000, "Game Winner"),
		MINUTE_PLAYED(10, "Minute Played");

		private final int coins;
		private final String reason;

		Award(int coins, String reason) {
			this.coins = coins;
			this.reason = reason;
		}

		public String getReason() {
			return reason;
		}

		public int getAmount() {
			return coins;
		}

	}

}
