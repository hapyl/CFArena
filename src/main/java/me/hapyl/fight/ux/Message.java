package me.hapyl.fight.ux;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Message {


    ;

    private static final String STAFF_PREFIX = Color.CORNFLOWER_BLUE.bold() + "sᴛᴀғғ";

    public static void info(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.DEFAULT + message, format);
    }

    public static void success(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.SUCCESS_DARKER + "✔ " + Color.SUCCESS + message, format);
    }

    public static void error(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.ERROR_DARKER + "✘ " + Color.ERROR + message, format);
    }

    public static void broadcast(@Nonnull String message, @Nullable Object... format) {
        Chat.broadcast(BFormat.format(message, format));
    }

    public static void broadcastStaff(@Nonnull String string, @Nullable Object... format) {
        Bukkit.getOnlinePlayers().forEach(online -> {
            final PlayerProfile profile = PlayerProfile.getProfile(online);

            if (profile == null || !profile.getRank().isStaff()) {
                return;
            }

            send(online, STAFF_PREFIX + " &b" + string, format);
        });
    }

    private static void send(CommandSender sender, String message, Object... format) {
        Chat.sendMessage(sender, BFormat.format(message, format));
    }

    // Enum for errors
    public enum Error implements MessageSender {

        PLAYER_NOT_ONLINE("{} is not online!"),
        NOT_ENOUGH_ARGUMENTS("Not enough arguments!"),
        INVALID_ENUMERABLE_ARGUMENT("Invalid argument! Try these: {}"),
        NOT_PERMISSIONS_NEED_RANK("You must be {} or higher to use this!"),
        CANNOT_FETCH_CRATE_ITEM(
                "Couldn't get your item! Try again before reporting this! (\"{}\")."
        ),
        CANNOT_FIND_CRATE("It doesn't seem that you have any {}!"),
        INVALID_USAGE("Invalid usage! {}."),


        _RESERVED("_RESERVED");

        private final String message;

        Error(String message) {
            this.message = message;
        }

        @Override
        public void send(@Nonnull CommandSender sender, @Nullable Object... format) {
            error(sender, message, format);
        }
    }

    // Enum for broadcasts
    public enum Broadcast implements MessageBroadcaster {

        CRATE_FLEX("&6&lCRATE! &a{} has gotten a {} item from {}!"),

        _RESERVED("_RESERVED"),

        ;

        private final String message;

        Broadcast(String message) {
            this.message = message;
        }

        @Override
        public void broadcast(@Nullable Object... format) {
            Message.broadcast(Color.DEFAULT + message, format);
        }
    }

}
