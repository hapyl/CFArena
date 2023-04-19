package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public class CurrencyReward extends Reward {

    private long coins;
    private long rubies;

    public CurrencyReward(String name) {
        super(name);
    }

    public CurrencyReward withCoins(long coins) {
        this.coins = coins;
        return this;
    }

    public CurrencyReward withRubies(long rubies) {
        this.rubies = rubies;
        return this;
    }

    public long getCoins() {
        return coins;
    }

    public long getRubies() {
        return rubies;
    }

    @Override
    public void display(Player player, ItemBuilder builder) {
        if (coins > 0) {
            builder.addLore("&a&l+&e&l" + coins + " Coins");
        }

        if (rubies > 0) {
            builder.addLore("&a&l+&d&l" + rubies + " Rubies");
        }
    }

    @Override
    public void grantReward(Player player) {
        final CurrencyEntry currency = PlayerDatabase.getDatabase(player).getCurrency();

        currency.add(Currency.COINS, coins);
        currency.add(Currency.RUBIES, rubies);
    }

    @Override
    public void revokeReward(Player player) {
        final CurrencyEntry currency = PlayerDatabase.getDatabase(player).getCurrency();

        currency.subtract(Currency.COINS, coins);
        currency.subtract(Currency.RUBIES, rubies);
    }
}
