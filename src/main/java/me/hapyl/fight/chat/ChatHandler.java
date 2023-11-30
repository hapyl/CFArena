package me.hapyl.fight.chat;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.database.rank.RankFormatter;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.data.PlayerData;
import me.hapyl.fight.game.profile.relationship.PlayerRelationship;
import me.hapyl.fight.game.profile.relationship.Relationship;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.infraction.InfractionType;
import me.hapyl.fight.infraction.PlayerInfraction;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Locale;

public class ChatHandler implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void handleChat(AsyncPlayerChatEvent ev) {
        final Player player = ev.getPlayer();
        final String message = ev.getMessage();
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            ev.setCancelled(true);
            return;
        }

        final PlayerInfraction infractions = profile.getInfractions();

        // Don't send the message
        ev.setCancelled(true);

        if (infractions.hasActive(InfractionType.CHAT_MUTE)) {
            Chat.sendCenterMessage(player, "&4You are currently muted!");
            Chat.sendCenterMessage(player, "&cYour mute expires in &bNaN &cyears!");
            Chat.sendMessage(player, "");
            Chat.sendMessage(player, "&8Infraction ID: Unspecified<HexID>");
            return;
        }

        final PlayerRank rank = profile.getRank();
        final PlayerData playerData = profile.getPlayerData();

        if (!rank.isStaff() && playerData.isLastMessageSimilarTo(message)) {
            Message.error(player, "You cannot say the same message twice!");
            return;
        }

        playerData.lastMessage = message;

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

    /**
     * Using scoreboard tags for now.
     */
    public boolean isMuted(Player player) {
        return player.getScoreboardTags().contains("Muted");
    }

    public void setMuted(Player player, boolean flag) {
        if (flag) {
            player.addScoreboardTag("Muted");
        }
        else {
            player.removeScoreboardTag("Muted");
        }
    }

    private void formatAndSendMessage(Player sender, String message, Player receiver) {
        // [Dead/Spec] [Class] [Color](Name): Message
        final PlayerProfile profile = PlayerProfile.getProfile(sender);
        if (profile == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder(profile.getDisplay().getDisplayName());

        // Tag receiver
        final String atReceiverName = ("@" + receiver.getName()).toLowerCase(Locale.ROOT);
        final String lowerCaseName = message.toLowerCase();

        if (lowerCaseName.contains(atReceiverName) && Settings.CHAT_PING.isEnabled(receiver)) {
            message = message.replace(atReceiverName, (ChatColor.YELLOW + "%s" + ChatColor.RESET).formatted(atReceiverName));
            PlayerLib.playSound(receiver, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f);
        }

        final PlayerRank rank = profile.getRank();
        final RankFormatter format = rank.getFormat();

        if (format.allowFormatting()) {
            message = Chat.format(message);
        }

        builder.append(Color.WHITE).append(": ").append(format.textColor()).append(message);
        receiver.sendMessage(builder.toString());
        //Chat.sendMessage(receiver, builder.toString());
    }

}
