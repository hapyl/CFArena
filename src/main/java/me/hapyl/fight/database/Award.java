package me.hapyl.fight.database;

import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Award {

    PLAYER_ELIMINATION("Opponent Eliminated.", 10, 5),
    PLAYER_ASSISTED("Assisted Elimination", 5, 2),
    GAME_WON("Winner", 100, 50),
    MINUTE_PLAYED("Minute Played", 1, 1),

    ;

    private final String reason;
    private final long coins;
    private final long exp;

    Award(String reason, long coins, long exp) {
        this.reason = reason;
        this.coins = coins;
        this.exp = exp;
    }

    public String getReason() {
        return reason;
    }

    public long getCoins() {
        return coins;
    }

    public long getExp() {
        return exp;
    }

    public void award(@Nonnull GamePlayer player) {
        if (Manager.current().isDebug()) {
            return;
        }

        final PlayerDatabase playerDatabase = player.getDatabase();
        final CurrencyEntry currency = playerDatabase.getCurrency();
        final ExperienceEntry experience = playerDatabase.getExperienceEntry();

        currency.add(Currency.COINS, coins);
        experience.add(ExperienceEntry.Type.EXP, exp);

        // Progress Stats
        final StatContainer stats = player.getStats();
        stats.addValue(StatType.COINS, coins);
        stats.addValue(StatType.EXP, exp);

        Chat.sendMessage(player.getPlayer(), "&a+ &6&l%s Coins &7& &b&l%s Exp &7(%s)", getCoins(), getExp(), getReason());
        PlayerLib.playSound(player.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
    }

    public void award(@Nonnull Player player) {
        award((GamePlayer) GamePlayer.getPlayer(player));
    }

}
