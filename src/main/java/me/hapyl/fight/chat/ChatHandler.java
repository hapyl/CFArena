package me.hapyl.fight.chat;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.data.PlayerProfileData;
import me.hapyl.fight.infraction.InfractionType;
import me.hapyl.fight.infraction.PlayerInfraction;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
        final PlayerProfileData playerData = profile.getPlayerData();

        if (!rank.isStaff() && playerData.isLastMessageSimilarTo(message)) {
            Notifier.error(player, "You cannot say the same message twice!");
            return;
        }

        ChatChannel.getChannelAndSendMessage(profile, message);
        playerData.lastMessage = message;
    }

}
