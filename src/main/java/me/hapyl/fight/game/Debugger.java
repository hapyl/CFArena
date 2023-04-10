package me.hapyl.fight.game;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Debugger {

    private static int debugUse = 0;

    /**
     * Indicates that this debug logger should not be removed in prod.
     */
    public static void keepLog(Object any, Object... format) {
        log(any, format);
        debugUse--;
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

    public static void wrap(Runnable runnable) {
        try {
            runnable.run();
            debugUse++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logIf(boolean condition, String any, Object... format) {
        if (condition) {
            log(any, format);
        }
    }

    public static void particle(Location location, Particle particle) {
        PlayerLib.spawnParticle(location, particle, 1);
    }

    private static void send(String string, Object... format) {
        final String formattedMessage = Chat.format("&c&lDEBUG &f" + string, format);

        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            Chat.sendMessage(player, formattedMessage);
        });

        Bukkit.getConsoleSender().sendMessage(formattedMessage);

        debugUse++;
    }

    public static int getDebugUse() {
        return debugUse;
    }
}
