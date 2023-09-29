package me.hapyl.fight.game.reward;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.Debug;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum CurrencyType {

    COINS(Currency.COINS),
    RUBY(Currency.RUBIES),
    CHEST_DUST(Currency.CHEST_DUST),
    ACHIEVEMENT_POINTS(Currency.ACHIEVEMENT_POINT),
    EXPERIENCE(null) {
        @Override
        public void increment(@Nonnull Player player, long value) {
            if (value <= 0) {
                return;
            }

            CF.getDatabase(player).getExperienceEntry().add(ExperienceEntry.Type.EXP, value);
        }

        @Override
        public void decrement(@Nonnull Player player, long value) {
            if (value <= 0) {
                return;
            }

            CF.getDatabase(player).getExperienceEntry().remove(ExperienceEntry.Type.EXP, value);
        }

        @Nonnull
        @Override
        public String format(long value) {
            return "&9 %,d Experience".formatted(value);
        }
    };

    private final Currency currency;

    CurrencyType(Currency currency) {
        this.currency = currency;
    }

    public void increment(@Nonnull Player player, long value) {
        if (value <= 0) {
            return;
        }

        Debug.info("increment call");
        Debug.info("currency =" + currency);
        Debug.info("value =" + value);

        CF.getDatabase(player).getCurrency().add(currency, value);
    }

    public void decrement(@Nonnull Player player, long value) {
        if (value <= 0) {
            return;
        }

        Debug.info("decrement call");

        CF.getDatabase(player).getCurrency().subtract(currency, value);
    }

    @Nonnull
    public String format(long value) {
        return currency.getColor() + "%,d".formatted(value) + " " + currency.getName();
    }
}
