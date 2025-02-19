package me.hapyl.fight.game.profile;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerSocialConversation {

    private static final String formatFrom = "&d&l\uD83D\uDD8A &dFrom %s&f: &7&o%s";
    private static final String formatTo = "&d&l\uD83D\uDD8A &dTo %s&f: &7&o%s";

    private final PlayerProfile profile;
    public Player lastMessenger;

    public PlayerSocialConversation(PlayerProfile profile) {
        this.profile = profile;
        this.lastMessenger = null;
    }

    @Nullable
    public Player getLastMessenger() {
        return lastMessenger;
    }

    public void sendMessage(@Nonnull PlayerProfile to, @Nonnull String message) {
        this.lastMessenger = to.getPlayer();
        to.getConversation().lastMessenger = profile.getPlayer();

        Chat.sendMessage(
                profile.getPlayer(),
                formatTo.formatted(to.display().toString(), message)
        );
    }

    public void receiveMessage(@Nonnull PlayerProfile from, @Nonnull String message) {
        this.lastMessenger = from.getPlayer();

        Chat.sendMessage(
                profile.getPlayer(),
                formatFrom.formatted(from.display().toString(), message)
        );
    }

    public static void talk(@Nonnull PlayerProfile sender, @Nonnull PlayerProfile receiver, @Nonnull String message) {
        if (message.isEmpty()) {
            Message.error(sender.getPlayer(), "Cannot send empty message!");
            return;
        }

        final PlayerSocialConversation senderConversation = sender.getConversation();
        final PlayerSocialConversation receiverConversation = receiver.getConversation();

        senderConversation.sendMessage(receiver, message);
        receiverConversation.receiveMessage(sender, message);
    }
}
