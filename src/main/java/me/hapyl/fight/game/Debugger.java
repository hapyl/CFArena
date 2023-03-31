package me.hapyl.fight.game;

import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Debugger {

    /**
     * Indicates that this debug logger should not be removed in prod.
     */
    public static void keepLog(Object any, Object... format) {
        log(any, format);
    }

    public static void log(Object any, Object... format) {
        send("&7" + any, format);
    }

    public static void warn(Object any, Object... format) {
        send("&e" + any, format);
    }

    public static void svr(Object any, Object... format) {
        send("&4&l" + any, format);
    }

    private static void send(String string, Object... format) {
        final String formattedMessage = Chat.format("&c&lDEBUG &f" + string, format);

        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            Chat.sendMessage(player, formattedMessage);
        });

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }
}
