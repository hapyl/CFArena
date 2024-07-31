package me.hapyl.fight.anticheat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.anticheat.trait.AntiTrait;
import me.hapyl.fight.anticheat.trait.AntiTraitCPS;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @implNote Because I hate everything, the 'AntiCheat' IS using event system!
 */
public final class AntiCheat {

    public static final String PREFIX = "&8[&4&lAC&8]";

    private static final PlayerRank BYPASS_RANK = PlayerRank.ADMIN;
    private static final PlayerRank NOTIFY_RANK = PlayerRank.ADMIN;

    private static AntiCheat INSTANCE;

    private final Map<Player, AntiData> playerData;
    private final List<AntiTrait> traits;

    private AntiCheat() {
        playerData = Maps.newHashMap();
        traits = Lists.newArrayList();

        // Register listener
        CF.registerEvents(new AntiListener());

        // Register traits
        registerTrait(new AntiTraitCPS());
    }

    public AntiData getData(@Nonnull Player player) {
        return playerData.computeIfAbsent(player, AntiData::new);
    }

    boolean removeData(@Nonnull Player player) {
        return this.playerData.remove(player) != null;
    }

    public void message(Object message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!PlayerRank.getRank(player).isOrHigher(NOTIFY_RANK)) {
                return;
            }

            Chat.sendMessage(player, PREFIX + " &7&o" + message);
        });

        // Don't forget about the console!
        // Yes this is the proper way of sending this kind of message,
        // it NEEDS to be sent to console, NOT logger
        Bukkit.getConsoleSender().sendMessage(Chat.format(PREFIX + " &7&o" + message));
    }

    private void registerTrait(AntiTrait trait) {
        traits.add(trait);
        CF.registerEvents(trait);
    }

    @Nonnull
    public static AntiCheat getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AntiCheat();
        }

        return INSTANCE;
    }

}
