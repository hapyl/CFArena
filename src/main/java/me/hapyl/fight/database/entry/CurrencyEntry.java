package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;

public class CurrencyEntry extends PlayerDatabaseEntry {

    public CurrencyEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
        this.setPath("currency");
    }

    public long get(Currency currency) {
        return fetchFromDocument(document -> document.get(currency.name(), 0L));
    }

    public void set(Currency currency, long value) {
        fetchDocument(document -> document.put(currency.name(), value));
    }

    public void add(Currency currency, long value) {
        set(currency, get(currency) - value);
    }

    public void subtract(Currency currency, long value) {
        set(currency, get(currency) - value);
    }

    public String getFormatted(Currency currency) {
        return String.format("%,d", get(currency));
    }

}
