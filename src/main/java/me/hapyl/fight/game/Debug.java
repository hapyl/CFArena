package me.hapyl.fight.game;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.util.ConstructorAccess;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
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

    public static void mi(Object... objects) {
        for (Object object : objects) {
            Debug.info(object);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T exception(Class<T> clazz, String message) {
        return ConstructorAccess.of(clazz)
                .tryGet(String.class)
                .tryGet()
                .tryInvoke(message)
                .getResultOrDefault((T) new RuntimeException(message));
    }

    public static void run(Runnable r) {
        if (!Manager.current().isDebug()) {
            return;
        }

        final StackTraceElement[] trace = new RuntimeException().getStackTrace();
        final StackTraceElement element = trace[1];

        send("&b$ " + element.toString().replace(element.getClassLoaderName() + "//", ""));
        r.run();
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
