package me.hapyl.fight.game.database.entry;

import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.database.DatabaseEntry;

public class CurrencyEntry extends DatabaseEntry {
	public CurrencyEntry(Database database) {
		super(database);
	}

	public long getCoins() {
		return this.getConfig().getLong("currency.coins", 0L);
	}

	public String getCoinsString() {
		return String.format("%,d", getCoins());
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

}
