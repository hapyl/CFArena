package me.hapyl.fight.game.profile;

import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PlayerSocialConversation {

    private static final String formatFrom = "&d&l\uD83D\uDD8A &dFrom %s&f: &7&o%s";
    private static final String formatTo = "&d&l\uD83D\uDD8A &dTo %s&f: &7&o%s";

    private final PlayerProfile profile;
    private UUID lastMessenger;

    public PlayerSocialConversation(PlayerProfile profile) {
        this.profile = profile;
        this.lastMessenger = null;
    }

    public void sendMessage(@Nonnull PlayerProfile to, @Nonnull String message) {
        to.getConversation().lastMessenger = profile.getUuid();

        final Player player = to.getPlayer();

        Chat.sendMessage(player, formatTo.formatted(profile.getDisplay().getNamePrefixed(), message));
    }

    public void receiveMessage(@Nonnull PlayerProfile from, @Nonnull String message) {
        this.lastMessenger = from.getUuid();

        Chat.sendMessage(profile.getPlayer(), formatFrom.formatted(from.getDisplay().getNamePrefixed(), message));
    }
}
