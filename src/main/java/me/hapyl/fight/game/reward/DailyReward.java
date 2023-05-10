package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DailyReward extends CurrencyReward {

    public static final long MILLIS_WHOLE_DAY = 86_400_000L;

    private final long bonusRubies;

    public DailyReward() {
        super("Daily Reward");

        withCoins(1000);
        withExp(10);

        this.bonusRubies = 1;
    }

    @Override
    public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final DailyRewardEntry entry = database.dailyRewardEntry;
        final boolean canClaim = entry.canClaim();

        if (canClaim) {
            builder.addLore("Today's Rewards:");
        }
        else {
            builder.addLore("Tomorrow's Rewards:");
        }

        builder.addLore();
        builder.addLore("&a+ &6%s Coins", getCoins());
        builder.addLore("&a+ &9%s Experience", getExp());

        if (entry.isBonusReward()) {
            builder.addLore("&a+ &c%s Ruby &a&lBONUS!", bonusRubies);
        }

        builder.addLore();
        builder.addLore("&7Current streak: &a%s &7days", entry.getStreak());
        builder.addLore();

        if (canClaim) {
            builder.addLore("&eClick to claim!");
        }
        else {
            builder.addLore("&eAlready claimed today!");
            builder.addLore("&eCome again in %s to claim.", formatDaily(player));
        }
    }

    @Override
    public void grantReward(@Nonnull Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final DailyRewardEntry entry = database.dailyRewardEntry;
        final int streak = entry.getStreak();

        final CurrencyEntry currency = database.getCurrency();
        currency.add(Currency.COINS, getCoins());

        final ExperienceEntry experience = database.getExperienceEntry();
        experience.add(ExperienceEntry.Type.EXP, getExp());

        if (entry.isBonusReward()) {
            currency.add(Currency.RUBIES, bonusRubies);
        }

        entry.markLastDailyReward();
        entry.increaseStreak();

        // Fx
        Chat.sendMessage(player, "&aClaimed daily rewards!");
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.0f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
    }

    @Override
    public void revokeReward(@Nonnull Player player) {
    }

    public static String formatDaily(Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final long date = database.dailyRewardEntry.nextDaily();

        long seconds = date / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return String.format("%02dh %02dm %02ds", hours, minutes % 60, seconds % 60);
    }
}
