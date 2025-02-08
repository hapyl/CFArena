package me.hapyl.fight.game.reward;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.TimeFormat;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.game.crate.Crates;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class DailyReward extends RepeatableReward {

    public static final long MILLIS_WHOLE_DAY = 86_400_000L;

    private final DailyRewardEntry.Type type;
    private final long bonusRubies;
    private final Map<Crates, Integer> dailyCrates;

    public DailyReward(DailyRewardEntry.Type type, long coins, long exp, long bonusRubies) {
        super("Daily %s Reward".formatted(Chat.capitalize(type)));

        withResource(RewardResource.COINS, coins);
        withResource(RewardResource.EXPERIENCE, exp);

        this.type = type;
        this.bonusRubies = bonusRubies;
        this.dailyCrates = Maps.newHashMap();
    }

    @Override
    public void appendDescription(@Nonnull Player player, @Nonnull RewardDescription description) {
        final PlayerDatabase database = CF.getDatabase(player);
        final DailyRewardEntry entry = database.dailyRewardEntry;

        description.appendIf(entry.isBonusReward(type), RewardResource.RUBY.format(bonusRubies) + " &a&lBONUS!");
        dailyCrates.forEach((crate, amount) -> description.append(crate.formatProduct((long) amount)));
    }

    @Override
    public void doGrant(@Nonnull Player player) {
        super.doGrant(player);
        final PlayerDatabase database = CF.getDatabase(player);

        final DailyRewardEntry rewardEntry = database.dailyRewardEntry;
        final CurrencyEntry currencyEntry = database.currencyEntry;

        if (rewardEntry.isBonusReward(type)) {
            currencyEntry.add(Currency.RUBIES, bonusRubies);
        }

        rewardEntry.markLastDailyReward(type);
        rewardEntry.increaseStreak(type);

        // Fx
        Message.success(player, "You have claimed your daily {%s} rewards!".formatted(type.toString()));

        Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.0f);
        Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
        Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
    }

    @Nonnull
    public String format(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final long nextDaily = database.dailyRewardEntry.nextDaily(type);

        return TimeFormat.format(nextDaily);
    }

}
