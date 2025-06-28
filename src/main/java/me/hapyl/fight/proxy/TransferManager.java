package me.hapyl.fight.proxy;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Labelled;
import me.hapyl.fight.util.Sha256;
import me.hapyl.fight.util.ThreadOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TransferManager {
    
    public static final String prefix = "&8[&3Proxy&8]&f ";
    public static final long payloadExpiration = 5_000L;
    
    private final Main main;
    
    public TransferManager(@Nonnull Main main) {
        this.main = main;
    }
    
    public void transfer(@Nonnull Player player, @Nonnull ServerType serverType) {
        final Main plugin = CF.getPlugin();
        final ServerType thisServer = plugin.thisServer();
        final Sha256 transferSecret = plugin.config().transferSecretSha256();
        
        // Check if transfers supported
        if (transferSecret == null) {
            message(player, "&4Transfers are not configured!");
            return;
        }
        
        // Check if already on the server
        if (thisServer == serverType) {
            message(player, "&4Already on this server!");
            return;
        }
        
        // Check if the game in progress
        if (Manager.current().isGameInProgress()) {
            message(player, "&4Cannot transfer during a game!");
            return;
        }
        
        // Transfer async
        ThreadOps.async(() -> {
            message(player, "&eAttempting to transfer you to '%s'...".formatted(serverType));
            
            // Validate the server is online
            if (!serverType.isBound()) {
                message(player, "&4Server '%s' is offline!".formatted(serverType));
                return;
            }
            
            final TransferPayload payload = new TransferPayload(thisServer, transferSecret, System.currentTimeMillis());
            
            // Store cookies
            payload.store(player);
            
            // Initiate transfer
            player.transfer(serverType.address().getHostName(), serverType.port());
        });
    }
    
    public void handleJoin(@Nonnull PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        
        TransferPayload.retrieve(player)
                       .thenAcceptAsync(payload -> {
                           // Most likely an illegal cookie due to /transfer or other non-api means of transferring
                           if (payload == null) {
                               disconnect(player, KickReason.ILLEGAL_TRANSFER);
                               return;
                           }
                           
                           final ServerType serverFrom = payload.server();
                           final Sha256 secret = payload.secret();
                           
                           // Validate secret matches
                           final Sha256 thisSecret = main.config().transferSecretSha256();
                           
                           if (!secret.equals(thisSecret)) {
                               disconnect(player, KickReason.INVALID_SECRET);
                               return;
                           }
                           
                           // Validate expiration
                           final long timestamp = payload.timestamp();
                           final long timePassed = System.currentTimeMillis() - timestamp;
                           
                           if (timePassed >= payloadExpiration) {
                               disconnect(player, KickReason.TRANSFER_EXPIRED);
                               return;
                           }
                           
                           // Successful transfer
                           broadcastTransfer(player, serverFrom, main.thisServer());
                           
                       })
                       .exceptionally(ex -> {
                           disconnect(player, ex::getMessage);
                           ex.printStackTrace();
                           
                           return null;
                       });
    }
    
    public void handleQuit(@Nonnull PlayerQuitEvent ev) {
    }
    
    public void message(@Nonnull CommandSender sender, @Nullable Object message) {
        Chat.sendMessage(sender, prefix + message);
    }
    
    private void broadcastTransfer(@Nonnull Player player, @Nonnull ServerType from, @Nonnull ServerType to) {
        Bukkit.getOnlinePlayers()
              .stream()
              .map(CF::getProfile)
              .filter(profile -> profile.getRank().isOrHigher(PlayerRank.ADMIN))
              .forEach(profile -> {
                  message(profile.getPlayer(), "&bTransferred &3%s&b from &3%s&b ➠ &3%s&b.".formatted(player.getName(), from.name().toLowerCase(), to.name().toLowerCase()));
              });
    }
    
    private void disconnect(@Nonnull Player player, @Nonnull Labelled reason) {
        ThreadOps.sync(() -> player.kick(
                Component.text()
                         .append(Component.text("Error joining server!", Color.ERROR, TextDecoration.BOLD))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("An error has occurred during transfer:", Color.RED))
                         .appendNewline()
                         .append(Component.text(reason.label(), Color.WHITE))
                         .build(),
                PlayerKickEvent.Cause.INVALID_COOKIE
        ));
    }
    
    public enum KickReason implements Labelled {
        ILLEGAL_TRANSFER,
        INVALID_SECRET,
        TRANSFER_EXPIRED;
        
        @Nonnull
        @Override
        public String label() {
            return name();
        }
    }
    
}
