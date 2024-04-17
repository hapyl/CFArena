package me.hapyl.fight.game.cosmetic.crate.convert;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.crate.CrateLocation;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.collection.Map2Long;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class CrateConvert implements Described {

    private static final int MAX_CONVERT_AT_ONCE = 100;

    private final Map2Long<Product<Long>> toConvert;
    //private final Map2Long<Crates> cratesToConvert;
    //private final Map2Long<Currency> currencyToConvert;
    private final String name;
    private final String description;

    private Crates convertProduct;
    private int convertProductAmount;

    public CrateConvert(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
        //this.cratesToConvert = new Map2Long<>();
        //this.currencyToConvert = new Map2Long<>();
        this.toConvert = new Map2Long<>();
        this.convertProduct = null;
        this.convertProductAmount = 1;
    }

    public boolean canConvert(@Nonnull Player player) {
        return canConvertTimes(player) > 0;
    }

    public int canConvertTimes(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final CrateEntry crateEntry = database.crateEntry;
        final CurrencyEntry currencyEntry = database.currencyEntry;

        if (convertProduct == null) {
            return 0;
        }

        final Map<Product<Long>, Long> playerItems = Maps.newHashMap();
        playerItems.putAll(crateEntry.mapped());
        playerItems.putAll(currencyEntry.mapped());

        int canConvertTimes = 0;

        for (int i = 0; i < MAX_CONVERT_AT_ONCE; i++, canConvertTimes++) {
            if (!toConvert.check((k, amount) -> {
                final long value = computeLongAndReturnPreviousValue(playerItems, k, amount);
                return value >= amount;
            })) {
                break;
            }
        }

        return canConvertTimes;
    }

    public boolean convert(@Nonnull Player player) {
        return convert(player, 1) == 1;
    }

    public int convert(@Nonnull Player player, int times) {
        final PlayerDatabase database = CF.getDatabase(player);
        final CrateEntry crateEntry = database.crateEntry;

        int converted = 0;

        for (int i = 0; i < times; i++, converted++) {
            if (!canConvert(player)) {
                // Only show the message if the first conversion and player does not have the items anymore.
                if (converted == 0) {
                    player.sendMessage(CrateLocation.PREFIX + Color.ERROR + "You don't have the required items to convert!");
                }
                break;
            }

            toConvert.forEach((d, amount) -> d.subtractProduct(database, amount));
            crateEntry.addCrate(convertProduct, convertProductAmount);
        }

        return converted;
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

    public CrateConvert setToConvert(@Nonnull Product<Long> convert, long amount) {
        toConvert.put(convert, amount);
        return this;
    }

    @Nullable
    public Crates getConvertProduct() {
        return convertProduct;
    }

    public CrateConvert setConvertProduct(Crates convertProduct) {
        this.convertProduct = convertProduct;
        return this;
    }

    public int getConvertProductAmount() {
        return convertProductAmount;
    }

    public CrateConvert setConvertProductAmount(int convertProductAmount) {
        this.convertProductAmount = convertProductAmount;
        return this;
    }

    @Override
    public String toString() {
        return getName() + "{%s = %sx%s}".formatted(toConvert, convertProductAmount, convertProduct);
    }

    public void appendRequirementsScaledToItemBuilder(@Nonnull ItemBuilder builder, int scale) {
        appendRequirementsScaledToItemBuilder(builder, scale, null);
    }

    public void appendRequirementsScaledToItemBuilder(@Nonnull ItemBuilder builder, int scale, @Nullable PlayerDatabase database) {
        toConvert.forEach((product, amount) -> {
            final long amountScaled = amount * scale;
            String suffix = "";

            if (database != null) {
                final Long playerAmount = product.getProduct(database);
                suffix = " " + CFUtils.checkmark(playerAmount >= amountScaled);
            }

            builder.addLore(" &8- &7%s".formatted(product.formatProduct(amountScaled)) + suffix);
        });
    }

    public void tryConvert(Player player, int times) {
        final int converted = convert(player, times);

        if (converted == times) {
            Chat.sendMessage(player, CrateLocation.PREFIX + Color.GREEN + "Converted %s crates!".formatted(times));
        }
        else {
            Chat.sendMessage(
                    player,
                    CrateLocation.PREFIX + Color.ERROR + "Was able to convert %s out of %s crates!".formatted(converted, times)
            );
        }

        // TODO -> Fx
    }

    private <K> long computeLongAndReturnPreviousValue(Map<K, Long> map, K k, long amount) {
        final Long oldValue = map.get(k);

        if (oldValue == null) {
            return 0;
        }

        map.compute(k, (kk, aLong) -> aLong == null ? 0 : aLong - amount);
        return oldValue;
    }
}
