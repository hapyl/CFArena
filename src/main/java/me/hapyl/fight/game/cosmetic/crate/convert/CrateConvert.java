package me.hapyl.fight.game.cosmetic.crate.convert;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.crate.CrateChest;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.collection.Map2Long;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CrateConvert implements Described {

    private final Map2Long<Crates> cratesToConvert;
    private final Map2Long<Currency> currencyToConvert;

    private final String name;
    private final String description;

    private Crates convertProduct;
    private int convertProductAmount;

    public CrateConvert(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
        this.cratesToConvert = new Map2Long<>();
        this.currencyToConvert = new Map2Long<>();
        this.convertProduct = null;
        this.convertProductAmount = 1;
    }

    public boolean canConvert(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final CrateEntry crateEntry = database.crateEntry;
        final CurrencyEntry currencyEntry = database.currencyEntry;

        if (convertProduct == null) {
            return false;
        }

        if (!cratesToConvert.check((crate, amount) -> crateEntry.getCrates(crate) >= amount)) {
            return false;
        }

        if (!currencyToConvert.check((currency, amount) -> currencyEntry.get(currency) >= amount)) {
            return false;
        }

        return true;
    }

    public void convert(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final CrateEntry crateEntry = database.crateEntry;
        final CurrencyEntry currencyEntry = database.currencyEntry;

        if (!canConvert(player)) {
            player.sendMessage(CrateChest.PREFIX + Color.ERROR + "You don't have the required items to use this!");
            return;
        }

        cratesToConvert.forEach(crateEntry::removeCrate);
        currencyToConvert.forEach(currencyEntry::subtract);

        crateEntry.addCrate(convertProduct, convertProductAmount);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public CrateConvert setConvertProduct(Crates convertProduct) {
        this.convertProduct = convertProduct;
        return this;
    }

    public CrateConvert setConvertProductAmount(int convertProductAmount) {
        this.convertProductAmount = convertProductAmount;
        return this;
    }

    public CrateConvert setCrateToConvert(@Nonnull Crates crate, long amount) {
        this.cratesToConvert.put(crate, amount);
        return this;
    }

    public CrateConvert setCurrencyToConvert(@Nonnull Currency currency, long amount) {
        this.currencyToConvert.put(currency, amount);
        return this;
    }
}
