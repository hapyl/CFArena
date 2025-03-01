package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.crate.convert.Product;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.FormattedEnum;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Currency implements FormattedEnum, Product<Long>, KeyedEnum {

    COINS(new Color("#FFD700"), "\uD83D\uDC08", "Catcoins") {
        @Override
        public void onIncrease(Player player, long value) {
            Registries.getAchievements().GAIN_COINS.addCompleteCount(player, (int) value);
        }
    },
    RUBIES(new Color("#9B111E"), "💎", "Rubies"),
    /**
     * @deprecated Crates are being discontinued
     */
    @Deprecated CHEST_DUST(new Color("#964B00"), "📦", "Dust"),

    ;

    private final Color color;
    private final String prefix;
    private final String name;

    Currency(Color color, String prefix, String name) {
        this.color = color;
        this.prefix = prefix;
        this.name = name;
    }

    /**
     * Called upon this currency increasing.
     *
     * @param player - Player, who this currency is increased for.
     * @param value  - Value by which this currency is increased by.
     */
    @EventLike
    public void onIncrease(Player player, long value) {
    }

    /**
     * Called upon this currency decreasing.
     *
     * @param player - Player, who this currency is decreased for.
     * @param value  - Value by which this currency is decreased by.
     */
    @EventLike
    public void onDecrease(Player player, long value) {
    }

    @Override
    public void subtractProduct(@Nonnull PlayerDatabase database, @Nonnull Long value) {
        database.currencyEntry.subtract(this, value);
    }

    @Nonnull
    @Override
    public Long getProduct(@Nonnull PlayerDatabase database) {
        return database.currencyEntry.get(this);
    }

    @Nonnull
    @Override
    public String formatProduct(@Nonnull Long amount) {
        return getFormatted() + " %,d".formatted(amount);
    }

    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return prefix;
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }

    @Nonnull
    public String getFormatted(@Nonnull Player player) {
        final CurrencyEntry currency = CF.getDatabase(player).currencyEntry;
        final String formatted = currency.getFormatted(this);

        return getPrefixColored() + " " + getColor() + formatted;
    }
}
