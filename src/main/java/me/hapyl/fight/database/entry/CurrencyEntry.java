package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import org.bson.Document;

public class CurrencyEntry extends PlayerDatabaseEntry {

    public CurrencyEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public long getCoins() {
        final Document currency = getConfig().get("currency", new Document());

        return currency.get("coins", 0L);
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
        getConfig().put("currency", new Document("coins", amount));
    }

}
