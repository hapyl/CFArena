package me.hapyl.fight.ux;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Message {

    /**
     * A prefix for staff.
     */
    String STAFF_PREFIX = "&6[&c&l🛡 &cStaff&6]";

    /**
     * Sends an info message to the given {@link CommandSender}.
     *
     * @param sender  - Sender.
     * @param message - Message.
     * @param format  - Format.
     */
    static void info(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.DEFAULT + message, colorFormat(Color.DEFAULT, format));
    }

    /**
     * Sends an success message to the given {@link CommandSender}.
     *
     * @param sender  - Sender.
     * @param message - Message.
     * @param format  - Format.
     */
    static void success(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.SUCCESS_DARKER + "✔ " + Color.SUCCESS + message, colorFormat(Color.SUCCESS, format));
    }

    /**
     * Sends an error message to the given {@link CommandSender}.
     *
     * @param sender  - Sender.
     * @param message - Message.
     * @param format  - Format.
     */
    static void error(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.ERROR_DARKER + "✘ " + Color.ERROR + message, colorFormat(Color.ERROR, format));
    }

    /**
     * Sends a warning message to the given {@link CommandSender}.
     *
     * @param sender  - Sender.
     * @param message - Message.
     * @param format  - Format.
     */
    static void warning(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, "&6[&l❗&6]&e " + message, colorFormat(Color.YELLOW, format));
    }

    /**
     * Broadcasts a message to all online players.
     *
     * @param message - Message.
     * @param format  - Format.
     */
    static void broadcast(@Nonnull String message, @Nullable Object... format) {
        Chat.broadcast(BFormat.format(message, format));
    }

    /**
     * Broadcasts a message to all online players whom rank is {@link PlayerRank#isStaff()}.
     *
     * @param string - Message.
     * @param format - Format
     */
    static void broadcastStaff(@Nonnull String string, @Nullable Object... format) {
        Bukkit.getOnlinePlayers().forEach(online -> {
            final PlayerProfile profile = PlayerProfile.getProfile(online);

            if (profile == null || !profile.getRank().isStaff()) {
                return;
            }

            send(online, STAFF_PREFIX + " &b" + string, format);
        });
    }

    private static Object[] colorFormat(Color color, Object... format) {
        if (format == null) {
            return null;
        }

        final String[] colored = new String[format.length];

        for (int i = 0; i < format.length; i++) {
            colored[i] = String.valueOf(format[i]) + color;
        }

        return colored;
    }

    private static void send(CommandSender sender, String message, Object... format) {
        Chat.sendMessage(sender, BFormat.format(message, format));
    }

    // Enum for errors
    enum Error implements MessageSender {

        PLAYER_NOT_ONLINE("{} is not online!"),
        NOT_ENOUGH_ARGUMENTS("Not enough arguments!"),
        INVALID_ENUMERABLE_ARGUMENT("Invalid argument! Try these: {}"),
        NOT_PERMISSIONS_NEED_RANK("You must be {} or higher to use this!"),
        CANNOT_FETCH_CRATE_ITEM(
                "Couldn't get your item! Try again before reporting this! (\"{}\")."
        ),
        CANNOT_FIND_CRATE("It doesn't seem that you have any {}!"),
        INVALID_USAGE("Invalid usage! {}."),

        ;

        private final String message;

        Error(String message) {
            this.message = message;
        }

        @Override
        public void send(@Nonnull CommandSender sender, @Nullable Object... format) {
            error(sender, message, format);
        }
    }

}
