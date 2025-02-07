package me.hapyl.fight.database.entry;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.EnumMappedEntry;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class CurrencyEntry extends PlayerDatabaseEntry implements EnumMappedEntry<Currency, Long> {

    public CurrencyEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "currency");
    }

    public long get(@Nonnull Currency currency) {
        return getValue(currency.getKeyAsString(), 0L);
    }

    public void set(@Nonnull Currency currency, long value) {
        setValue(currency.getKeyAsString(), value);
    }

    public void add(@Nonnull Currency currency, long value) {
        set(currency, get(currency) + value);

        final Player player = getOnlinePlayer();

        if (player != null) {
            currency.onIncrease(player, value);
        }
    }

    public void subtract(@Nonnull Currency currency, long value) {
        set(currency, get(currency) - value);

        final Player player = getOnlinePlayer();

        if (player != null) {
            currency.onDecrease(player, value);
        }
    }

    @Nonnull
    public String getFormatted(@Nonnull Currency currency) {
        return String.format("%,d", get(currency));
    }

    public boolean has(@Nonnull Currency currency, long value) {
        return get(currency) >= value;
    }

    @Nonnull
    @Override
    public Currency[] enumValues() {
        return Currency.values();
    }

    @Nonnull
    @Override
    public Long getMappedValue(@Nonnull Currency currency) {
        return get(currency);
    }

    @Nonnull
    @Override
    public Map<Currency, Long> mapped() {
        final Map<Currency, Long> map = Maps.newLinkedHashMap();

        for (Currency currency : Currency.values()) {
            final long amount = get(currency);

            if (amount > 0) {
                map.put(currency, amount);
            }
        }

        return map;
    }
}
