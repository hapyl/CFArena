package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.crate.convert.Product;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.FormattedEnum;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Currency implements FormattedEnum, Product<Long> {

    COINS(new Color("#FFD700"), "🪙", "Coins") {
        @Override
        public void onIncrease(Player player, long value) {
            Achievements.GAIN_COINS.addProgress(player, (int) value);
        }
    },
    RUBIES(new Color("#9B111E"), "💎", "Rubies"),
    CHEST_DUST(new Color("#964B00"), "📦", "Dust"),
    ACHIEVEMENT_POINT(Color.ROYAL_BLUE, "\uD83C\uDF1F", "Achievement Points"),

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
    @Event
    public void onIncrease(Player player, long value) {
    }

    /**
     * Called upon this currency decreasing.
     *
     * @param player - Player, who this currency is decreased for.
     * @param value  - Value by which this currency is decreased by.
     */
    @Event
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

    public String getPath() {
        return name().toLowerCase();
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
    public String getFormatted(Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return "null";
        }

        final CurrencyEntry currency = profile.getDatabase().currencyEntry;
        final String formatted = currency.getFormatted(this);

        return getPrefixColored() + " " + getColor() + formatted;
    }
}
