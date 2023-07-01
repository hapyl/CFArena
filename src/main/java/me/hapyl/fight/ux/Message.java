package me.hapyl.fight.ux;

import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BFormat;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Message {


    ;

    public static void info(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.DEFAULT + message, format);
    }

    public static void success(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.SUCCESS_DARKER + "✔ " + Color.SUCCESS + message, format);
    }

    public static void error(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        send(sender, Color.ERROR_DARKER + "✘ " + Color.ERROR + message, format);
    }

    private static void send(CommandSender sender, String message, Object... format) {
        Chat.sendMessage(sender, BFormat.format(message, format));
    }

    public enum Error implements Sendable {

        PLAYER_NOT_ONLINE("{} is not online!"),
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

    public interface Sendable {

        void send(@Nonnull CommandSender sender, @Nullable Object... format);

    }

}
