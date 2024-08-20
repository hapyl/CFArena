package me.hapyl.fight.chat;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.database.rank.RankFormatter;
import me.hapyl.fight.emoji.Emojis;
import me.hapyl.fight.filter.ProfanityFilter;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.relationship.PlayerRelationship;
import me.hapyl.fight.game.profile.relationship.Relationship;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatChannel {

    PUBLIC(""),

    TEAM("#") {
        @Override
        public void processMessage(@Nonnull PlayerProfile profile, @Nonnull String message) {
            final Player player = profile.getPlayer();
            final GameTeam playerTeam = GameTeam.getEntryTeam(Entry.of(player));

            if (playerTeam == null) {
                Notifier.error(player, "You are not in a team!");
                return;
            }

            final String teamMessage = "%1$s[%2$s%1$s] %3$s&f: &o%4$s".formatted(
                    playerTeam.getColor(),
                    playerTeam.getFlagColored(),
                    player.getName(),
                    message
            ).trim();

            playerTeam.getBukkitPlayers().forEach(teammate -> Chat.sendMessage(teammate, teamMessage));
        }

        @Override
        public boolean allowProfanity() {
            return true;
        }
    },

    STAFF("!") {
        @Override
        public boolean canSend(@Nonnull PlayerProfile profile) {
            return profile.getRank().isStaff();
        }

        @Override
        public boolean allowProfanity() {
            return true;
        }

        @Override
        public void processMessage(@Nonnull PlayerProfile profile, @Nonnull String message) {
            Notifier.broadcastStaff("&c%s: &f%s".formatted(profile.getPlayer().getName(), message));
        }
    };

    private static final Pattern ggPattern = Pattern.compile("\\b(gg|ggs|good game)\\b", Pattern.CASE_INSENSITIVE);
    private final String prefix;

    ChatChannel(String prefix) {
        this.prefix = prefix;
    }

    public boolean canSend(@Nonnull PlayerProfile profile) {
        return true;
    }

    public boolean allowProfanity() {
        return false;
    }

    public void processMessage(@Nonnull PlayerProfile profile, @Nonnull String message) {
        final Player player = profile.getPlayer();

        Bukkit.getOnlinePlayers().forEach(online -> {
            final PlayerProfile otherProfile = PlayerProfile.getProfile(online);

            if (otherProfile == null) {
                return;
            }

            final PlayerRelationship playerRelationship = otherProfile.getPlayerRelationship();
            final Relationship relationship = playerRelationship.getRelationship(player);

            if (relationship == Relationship.BLOCKED) {
                return;
            }

            formatAndSendMessage(player, message, online);
        });
    }

    @Override
    public String toString() {
        return prefix;
    }

    private void formatAndSendMessage(Player sender, String message, Player receiver) {
        final PlayerProfile profile = PlayerProfile.getProfile(sender);

        if (profile == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder(profile.getDisplay().toString());

        // Tag receiver
        final String atReceiverName = ("@" + receiver.getName()).toLowerCase(Locale.ROOT);
        final String lowerCaseName = message.toLowerCase();

        if (lowerCaseName.contains(atReceiverName) && Settings.CHAT_PING.isEnabled(receiver)) {
            message = message.replace(atReceiverName, (ChatColor.YELLOW + atReceiverName + ChatColor.RESET));
            PlayerLib.playSound(receiver, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f);
        }

        final PlayerRank rank = profile.getRank();
        final RankFormatter format = rank.getFormat();

        if (format.allowFormatting()) {
            message = Chat.format(message);
        }

        // FIXME (Tue, Aug 20 2024 @xanyjl): For some reason the @ does not work but it worked for me???
        builder.append(Color.WHITE).append(": ").append(format.textColor()).append(message);
        receiver.sendMessage(builder.toString());
        //Chat.sendMessage(receiver, builder.toString());
    }

    @Nonnull
    public static ChatChannel getChannelByMessage(@Nonnull String message) {
        message = message.trim();

        for (ChatChannel value : values()) {
            if (value.prefix.isEmpty()) {
                continue;
            }

            if (message.startsWith(value.prefix)) {
                return value;
            }
        }

        return PUBLIC;
    }

    public static void getChannelAndSendMessage(@Nonnull PlayerProfile profile, @Nonnull String message) {
        ChatChannel channel = getChannelByMessage(message);

        if (!channel.canSend(profile)) {
            channel = PUBLIC;
        }

        if (!channel.prefix.isEmpty()) {
            message = message.substring(1).trim();
        }

        final PlayerRank rank = profile.getRank();

        if (!channel.allowProfanity() && !rank.isStaff()) {
            message = ProfanityFilter.replaceProfane(message);
        }

        // Emoji
        message = Emojis.replaceEmojis(message, profile);

        // Golden GG
        final Matcher matcher = ggPattern.matcher(message);

        if (matcher.find()) {
            final boolean isGoldenGg = Manager.current().goldenGg(profile.getPlayer());

            if (isGoldenGg) {
                message = matcher.replaceFirst(match -> {
                    final String group = match.group();

                    return "&6" + group + profile.getRank().getFormat().textColor();
                });
            }
        }

        channel.processMessage(profile, message);
    }

}
