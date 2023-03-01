package me.hapyl.fight.game.database;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.StatContainer;
import me.hapyl.fight.game.database.entry.CurrencyEntry;
import me.hapyl.fight.game.database.entry.ExperienceEntry;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Award {

    PLAYER_ELIMINATION("Opponent Eliminated.", 100, 5),
    GAME_WON("Winner", 1000, 50),
    MINUTE_PLAYED("Minute Played", 10, 1),

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
        if (player.isAbstract() || Manager.current().isDebug()) {
            return;
        }

        final Database database = player.getDatabase();
        final CurrencyEntry currency = database.getCurrency();
        final ExperienceEntry experience = database.getExperienceEntry();

        currency.addCoins(coins);
        experience.add(ExperienceEntry.Type.EXP, exp);

        // Progress Stats
        final StatContainer stats = player.getStats();
        stats.addValue(StatContainer.Type.COINS, coins);
        stats.addValue(StatContainer.Type.EXP, exp);

        Chat.sendMessage(player.getPlayer(), "&a+ &6&l%s Coins &7& &b&l%s Exp &7(%s)", getCoins(), getExp(), getReason());
        PlayerLib.playSound(player.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
    }

    public void award(@Nonnull Player player) {
        award((GamePlayer) GamePlayer.getPlayer(player));
    }

}
