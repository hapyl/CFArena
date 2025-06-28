package me.hapyl.fight;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.SoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A notifier that sends/broadcasts messages.
 * <br>
 * This notifier supports argument coloring:
 * <ul>
 *     <li>Surround the argument with <code>{}</code>:
 *     <pre>{@code
 *       Message.info(sender, "Hello, {%s}!".formatted(sender.getName());
 *       Message.success(sender, "This was {successful}!");
 *     }</pre>
 * </ul>
 */
@ApiStatus.NonExtendable
public interface Message {

    /**
     * A prefix for staff.
     */
    String STAFF_PREFIX = "&6[&c&l🛡 &cStaff&6]";

    /**
     * Info {@link Channel}.
     */
    Channel INFO = new Channel(Color.DEFAULT.toString(), Color.WHITE, Color.DEFAULT);

    /**
     * Success {@link Channel}.
     */
    Channel SUCCESS = new Channel(Color.SUCCESS_DARKER + "✔ " + Color.SUCCESS, Color.GREEN, Color.SUCCESS);

    /**
     * Error {@link Channel}.
     */
    Channel ERROR = new Channel(Color.ERROR_DARKER + "✘ " + Color.ERROR, Color.RED, Color.ERROR) {
        @Nonnull
        @Override
        protected Tuple<Sound, Float> getSound() {
            return Tuple.of(Sound.ENTITY_VILLAGER_NO, 1.0f);
        }
    };

    /**
     * Warning {@link Channel}.
     */
    Channel WARNING = new Channel("&6&l❗&e ", Color.GOLD, Color.YELLOW);

    /**
     * Staff {@link Channel}.
     * <br>
     * Only players whoever {@link PlayerRank} is at least {@link PlayerRank#isStaff()} can receive messages from this channel.
     */
    Channel STAFF = new Channel(STAFF_PREFIX + " &b", Color.DARK_AQUA, Color.AQUA) {

        @Override
        public boolean shouldSendTo(@Nonnull CommandSender sender) {
            if (!(sender instanceof Player player)) {
                return false;
            }

            return CF.getProfile(player).getRank().isStaff();
        }

    };

    static void title(@Nonnull CommandSender sender, @Nonnull String title, @Nonnull String subtitle) {
        if (!(sender instanceof Player player)) {
            return;
        }

        Chat.sendTitle(player, title, subtitle, 10, 30, 5);
        PlayerLib.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.25f);
    }

    static void info(@Nonnull CommandSender sender, @Nonnull String message) {
        INFO.send(sender, message);
    }

    static void success(@Nonnull CommandSender sender, @Nonnull String message) {
        SUCCESS.send(sender, message);
    }

    static void error(@Nonnull CommandSender sender, @Nonnull String message) {
        ERROR.send(sender, message);
    }

    static void warning(@Nonnull CommandSender sender, @Nonnull String message) {
        WARNING.send(sender, message);
    }

    static void broadcastStaff(@Nonnull String message) {
        STAFF.broadcast(message);
    }

    /**
     * Plays the given {@link Sound} to the given {@link Player} with the given pitch.
     *
     * @param player - Player to play the sound to.
     * @param sound  - Sound to play.
     * @param pitch  - Pitch of the sound.
     */
    static void sound(@Nonnull Player player, @Nonnull Sound sound, float pitch) {
        PlayerLib.playSound(player, sound, pitch);
    }

    /**
     * Plays the given {@link SoundEffect} to the given {@link Player}.
     *
     * @param player - Player to play the sound effect to.
     * @param effect - Sound effect to play.
     */
    static void sound(@Nonnull Player player, @Nonnull SoundEffect effect) {
        effect.play(player);
    }


    /**
     * An {@link Enum} hard-coded errors.
     */
    enum Error implements MessageSender {

        PLAYER_NOT_ONLINE("{%s} is not online!"),
        NOT_ENOUGH_ARGUMENTS("Not enough arguments!"),
        INVALID_ENUMERABLE_ARGUMENT("Invalid argument! Try these: {%s}"),
        NOT_PERMISSIONS_NEED_RANK("You must be {%s} or higher to use this!"),
        CANNOT_FETCH_CRATE_ITEM(
                "Couldn't get your item! Try again before reporting this! (\"{%s}\")."
        ),
        CANNOT_FIND_CRATE("It doesn't seem that you have any {%s}!"),
        INVALID_USAGE("Invalid usage! {%s}."),

        ;

        private final String message;

        Error(String message) {
            this.message = message;
        }

        @Override
        public void send(@Nonnull CommandSender sender, @Nullable Object... format) {
            ERROR.send(sender, message.formatted(format));
        }
    }

    interface MessageSender {

        void send(@Nonnull CommandSender sender, @Nullable Object... format);

    }

    class Channel {

        private final String prefix;
        private final Color[] colors;

        Channel(String prefix, Color formatPrefix, Color formatSuffix) {
            this.prefix = prefix;
            this.colors = new Color[] { formatPrefix, formatSuffix };
        }

        public final void send(@Nonnull CommandSender sender, @Nonnull String message) {
            if (!shouldSendTo(sender)) {
                return;
            }

            Chat.sendMessage(sender, prefix + colorFormat(message));
        }

        public final void sendWithSound(@Nonnull CommandSender player, @Nonnull String message) {
            send(player, message);
            sound(player);
        }

        public final void broadcast(@Nonnull String message) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                send(player, message);
            });
        }

        public final void broadcastWithSound(@Nonnull String message) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                sendWithSound(player, message);
            });
        }

        public final void sound(@Nonnull CommandSender sender) {
            final Tuple<Sound, Float> sound = getSound();

            if (sound == null || !(sender instanceof Player player) || !shouldSendTo(sender)) {
                return;
            }

            PlayerLib.playSound(player, sound.a(), sound.b());
        }

        protected boolean shouldSendTo(@Nonnull CommandSender sender) {
            return true;
        }

        @Nullable
        protected Tuple<Sound, Float> getSound() {
            return null;
        }

        private String colorFormat(String string) {
            return string.replaceAll("\\{([^}]*)}", colors[0] + "$1" + colors[1]);
        }

    }
}
