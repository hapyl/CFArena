package me.hapyl.fight.game.reward;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.CollectionUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class CurrencyReward extends SimpleReward {

    private final Map<CurrencyType, Long> currencyMap;

    protected CurrencyReward(@Nonnull String name) {
        super(name);

        this.currencyMap = Maps.newHashMap();
    }

    public long get(@Nonnull CurrencyType currency) {
        return currencyMap.getOrDefault(currency, 0L);
    }

    public CurrencyReward with(@Nonnull CurrencyType currency, long value) {
        currencyMap.put(currency, value);
        return this;
    }

    public CurrencyReward withCoins(long coins) {
        return with(CurrencyType.COINS, coins);
    }

    public CurrencyReward withRubies(long rubies) {
        return with(CurrencyType.RUBY, rubies);
    }

    public CurrencyReward withExp(long exp) {
        return with(CurrencyType.EXPERIENCE, exp);
    }

    public long getCoins() {
        return get(CurrencyType.COINS);
    }

    public long getRubies() {
        return get(CurrencyType.RUBY);
    }

    public long getExp() {
        return get(CurrencyType.EXPERIENCE);
    }

    @Nonnull
    @Override
    public RewardDescription getDescription(@Nonnull Player player) {
        final RewardDescription display = new RewardDescription();

        for (CurrencyType currency : CurrencyType.values()) {
            final long value = get(currency);

            display.addIf(value > 0, currency.format(value));
        }

        return display;
    }

    @Override
    public void grant(@Nonnull Player player) {
        currencyMap.forEach((currency, value) -> {
            currency.increment(player, value);
        });
    }

    @Override
    public void revoke(@Nonnull Player player) {
        currencyMap.forEach(((currency, value) -> {
            currency.decrement(player, value);
        }));
    }

}
