package me.hapyl.fight.game;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Debug {

    /**
     * Indicates that this debug logger should not be removed in prod.
     */
    public static void keepInfo(Object any, Object... format) {
        info(any, format);
    }

    public static void info(Object any, Object... format) {
        send("&7" + any, format);
    }

    public static void warn(Object any, Object... format) {
        send("&e" + any, format);
    }

    public static void severe(Object any, Object... format) {
        send("&4&l" + any, format);
    }

    public static void wrap(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void infoIf(boolean condition, String any, Object... format) {
        if (condition) {
            info(any, format);
        }
    }

    public static void particle(Location location, Particle particle) {
        PlayerLib.spawnParticle(location, particle, 1);
    }

    private static String now() {
        return DateTimeFormatter.ofPattern("hh:mm:ss").format(LocalTime.now());
    }

    private static void send(String string, Object... format) {
        final String formattedMessage = Chat.format("&c&lDEBUG &8" + now() + " &f" + string, format);

        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            Chat.sendMessage(player, formattedMessage);
        });

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }
}
