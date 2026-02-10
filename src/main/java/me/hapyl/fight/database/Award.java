package me.hapyl.fight.database;

import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.util.StrBuilder;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Award {

    PLAYER_ELIMINATION("Opponent Eliminated.", 10, 5),
    PLAYER_ASSISTED("Assisted Elimination", 5, 2),
    GAME_WON("Winner", 100, 50),
    MINUTE_PLAYED("Minute Played", 1, 1),
    GG("Karma", 0, 5),

    ;

    private final String reason;
    private final long coins;
    private final long exp;

    Award(String reason, long coins, long exp) {
        this.reason = reason;
        this.coins = coins;
        this.exp = exp;
    }

    @Nonnull
    public String getReason() {
        return reason;
    }

    public long getCoins() {
        return coins;
    }

    public long getExp() {
        return exp;
    }

    public void award(@Nonnull PlayerProfile profile) {
        final PlayerDatabase playerDatabase = profile.getDatabase();
        final CurrencyEntry currency = playerDatabase.currencyEntry;
        final ExperienceEntry experience = playerDatabase.experienceEntry;

        currency.add(Currency.COINS, coins);
        experience.add(ExperienceEntry.Type.EXP, exp);

        final Player player = profile.getPlayer();
        final StrBuilder builder = new StrBuilder("&a+ ");

        builder.appendIf("%s Coins &8& ".formatted(Currency.COINS.getColor().color(coins)), coins > 0);
        builder.appendIf("&9%s Experience".formatted(exp), exp > 0);

        builder.append(" &7(%s)".formatted(reason));

        Chat.sendMessage(player, builder.toString());
        PlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
    }

    public void award(@Nonnull GamePlayer player) {
        if (Manager.current().isDebug()) {
            return;
        }

        this.award(player.getProfile());

        // Progress Stats
        final StatContainer stats = player.getStats();

        stats.addValue(StatType.COINS, coins);
        stats.addValue(StatType.EXP, exp);
    }

}
