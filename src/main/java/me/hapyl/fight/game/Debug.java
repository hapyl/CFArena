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
    public static void keepInfo(Object message) {
        info(message);
    }

    /**
     * Sends info debug in gray text.
     *
     * @param message - Message.
     */
    public static void info(Object message) {
        send("&7" + message);
    }

    /**
     * Sends info debug in yellow text.
     *
     * @param message - Message.
     */
    public static void warn(Object message) {
        send("&e" + message);
    }

    /**
     * Sends info debug in dark red bold text.
     *
     * @param message - Message.
     */
    public static void severe(Object message) {
        send("&4&l" + message);
    }

    public static void particle(Location location, Particle particle) {
        PlayerLib.spawnParticle(location, particle, 1);
    }

    public static void uncommentMe(Object reason) {
        send("&7// UNCOMMENT ME // " + reason);
    }

    private static String now() {
        return DateTimeFormatter.ofPattern("hh:mm:ss").format(LocalTime.now());
    }

    private static void send(String string) {
        final String formattedMessage = Chat.format("&c&lDEBUG &8" + now() + " &f" + string);

        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            Chat.sendMessage(player, formattedMessage);
        });

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }

}
