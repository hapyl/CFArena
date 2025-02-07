package me.hapyl.fight.game.reward;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.TimeFormat;
import me.hapyl.fight.CF;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.game.crate.Crates;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class DailyReward extends CurrencyReward {

    public static final long MILLIS_WHOLE_DAY = 86_400_000L;

    private final DailyRewardEntry.Type type;
    private final long bonusRubies;
    private final Map<Crates, Integer> dailyCrates;

    public DailyReward(DailyRewardEntry.Type type, long coins, long exp, long bonusRubies) {
        super("Daily %s Reward".formatted(Chat.capitalize(type)));

        withCoins(coins);
        withExp(exp);

        this.type = type;
        this.bonusRubies = bonusRubies;
        this.dailyCrates = Maps.newHashMap();
    }

    @Nonnull
    public DailyReward setCrate(@Nonnull Crates crate, int amount) {
        dailyCrates.put(crate, amount);
        return this;
    }

    @Override
    @Nonnull
    public RewardDescription getDescription(@Nonnull Player player) {
        final RewardDescription display = super.getDescription(player);
        final PlayerDatabase database = CF.getDatabase(player);
        final DailyRewardEntry entry = database.dailyRewardEntry;

        display.addIf(entry.isBonusReward(type), CurrencyType.RUBY.format(bonusRubies) + " &a&lBONUS!");

        dailyCrates.forEach((crate, amount) -> {
            display.add(crate.formatProduct((long) amount));
        });

        return display;
    }

    @Override
    public void grant(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);

        final DailyRewardEntry rewardEntry = database.dailyRewardEntry;
        final CurrencyEntry currencyEntry = database.currencyEntry;
        final CrateEntry crateEntry = database.crateEntry;

        super.grant(player);

        if (rewardEntry.isBonusReward(type)) {
            currencyEntry.add(Currency.RUBIES, bonusRubies);
        }

        dailyCrates.forEach(crateEntry::addCrate);

        rewardEntry.markLastDailyReward(type);
        rewardEntry.increaseStreak(type);

        // Fx
        Notifier.success(player, "You have claimed your daily {%s} rewards!".formatted(type.toString()));

        Notifier.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.0f);
        Notifier.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
        Notifier.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
    }

    @Override
    public void revoke(@Nonnull Player player) {
    }

    @Nonnull
    public String format(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final long nextDaily = database.dailyRewardEntry.nextDaily(type);

        return TimeFormat.format(nextDaily);
    }

}
