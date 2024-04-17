package me.hapyl.fight.game.reward;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.util.TimeFormat;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class DailyReward extends CurrencyReward {

    public static final long MILLIS_WHOLE_DAY = 86_400_000L;

    private final long bonusRubies;
    private final Map<Crates, Integer> dailyCrates;

    private DailyRewardEntry.Type type;

    public DailyReward(long coins, long exp, long bonusRubies) {
        super();

        withCoins(coins);
        withExp(exp);

        this.bonusRubies = bonusRubies;
        this.dailyCrates = Maps.newHashMap();
    }

    public void setType(DailyRewardEntry.Type type) {
        this.type = type;
    }

    @Nonnull
    public DailyReward setCrate(@Nonnull Crates crate, int amount) {
        dailyCrates.put(crate, amount);
        return this;
    }

    @Override
    @Nonnull
    public RewardDisplay getDisplay(@Nonnull Player player) {
        final RewardDisplay display = super.getDisplay(player);
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final DailyRewardEntry entry = database.dailyRewardEntry;

        display.addIf(entry.isBonusReward(type), CurrencyType.RUBY.format(bonusRubies) + " &a&lBONUS!");

        dailyCrates.forEach((crate, amount) -> {
            display.add(crate.formatProduct((long) amount));
        });

        return display;
    }

    @Override
    public void grant(@Nonnull Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);

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
        Chat.sendMessage(player, "&aYou have claimed your daily %s&a rewards!".formatted(type.toString()));

        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.0f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
    }

    @Override
    public void revoke(@Nonnull Player player) {
    }

    @Nonnull
    public String format(@Nonnull Player player) {
        final PlayerDatabase database = getDatabase(player);
        final long nextDaily = database.dailyRewardEntry.nextDaily(type);

        return TimeFormat.format(nextDaily);
    }

}
