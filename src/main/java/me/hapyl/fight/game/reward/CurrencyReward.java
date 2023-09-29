package me.hapyl.fight.game.reward;

import com.google.common.collect.Maps;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class CurrencyReward extends Reward implements OneTimeReward {

    private final Map<CurrencyType, Long> currencyMap;

    public CurrencyReward(String name) {
        super(name);
        this.currencyMap = Maps.newHashMap();
    }

    public CurrencyReward with(@Nonnull CurrencyType currency, long value) {
        currencyMap.put(currency, value);
        return this;
    }

    public long get(@Nonnull CurrencyType currency) {
        return currencyMap.getOrDefault(currency, 0L);
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

    @Override
    public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        for (CurrencyType currency : CurrencyType.values()) {
            final long value = get(currency);

            if (value > 0) {
                builder.addLore(BULLET + currency.format(value));
            }
        }
    }

    @Override
    public void grantReward(@Nonnull Player player) {
        currencyMap.forEach((currency, value) -> {
            currency.increment(player, value);
        });
    }

    @Override
    public void revokeReward(@Nonnull Player player) {
        currencyMap.forEach(((currency, value) -> {
            currency.decrement(player, value);
        }));
    }

    @Nonnull
    public static CurrencyReward create() {
        return new CurrencyReward(UUID.randomUUID().toString());
    }
}
