package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.util.Booleans;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class CurrencyReward extends Reward {

    private long coins;
    private long rubies;
    private long exp;

    public CurrencyReward(String name) {
        super(name);
    }

    public static CurrencyReward create() {
        return new CurrencyReward(UUID.randomUUID().toString());
    }

    public CurrencyReward withCoins(long coins) {
        this.coins = coins;
        return this;
    }

    public CurrencyReward withRubies(long rubies) {
        this.rubies = rubies;
        return this;
    }

    public CurrencyReward withExp(long exp) {
        this.exp = exp;
        return this;
    }

    public long getCoins() {
        return coins;
    }

    public long getRubies() {
        return rubies;
    }

    public long getExp() {
        return exp;
    }

    @Override
    public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        builder.addLoreIf("&a+ &6" + coins + " Coins", coins > 0);
        builder.addLoreIf("&a+ &9" + exp + " Experience", exp > 0);
        builder.addLoreIf("&a+ &c" + rubies + " Rubies", rubies > 0);
    }

    @Override
    public void grantReward(@Nonnull Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final CurrencyEntry currency = database.currencyEntry;

        Booleans.ifTrue(coins > 0, () -> currency.add(Currency.COINS, coins));
        Booleans.ifTrue(exp > 0, () -> database.experienceEntry.add(ExperienceEntry.Type.EXP, exp));
        Booleans.ifTrue(rubies > 0, () -> currency.add(Currency.RUBIES, rubies));
    }

    @Override
    public void revokeReward(@Nonnull Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final CurrencyEntry currency = PlayerDatabase.getDatabase(player).getCurrency();

        Booleans.ifTrue(coins > 0, () -> currency.subtract(Currency.COINS, coins));
        Booleans.ifTrue(rubies > 0, () -> currency.subtract(Currency.RUBIES, rubies));
        Booleans.ifTrue(exp > 0, () -> database.experienceEntry.remove(ExperienceEntry.Type.EXP, exp));
    }
}
