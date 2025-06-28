package me.hapyl.fight.invite;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.Disposable;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.CFUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class PlayerInvite extends BukkitRunnable implements Acceptable, Disposable {
    
    private static final Multimap<UUID, PlayerInvite> playerInvites = MultimapBuilder.hashKeys().hashSetValues().build();
    
    private static final int inviteExpireAfter = 600;
    private static final int inviteLimitPerInviter = 5;
    
    private final UUID uuid;
    private final PlayerProfile player;
    private final PlayerProfile invitee;
    private final Identifier identifier;
    
    private PlayerInvite(Player player, Player invitee, Identifier identifier) {
        this.uuid = UUID.randomUUID();
        this.player = CF.getProfile(player);
        this.invitee = CF.getProfile(invitee);
        this.identifier = identifier;
        
        runTaskLater(CF.getPlugin(), inviteExpireAfter);
    }
    
    @Nonnull
    public Player player() {
        return player.getPlayer();
    }
    
    @Nonnull
    public Player invitee() {
        return invitee.getPlayer();
    }
    
    @Override
    @Nonnull
    public Response onAccept(@Nonnull Player player) {
        return Response.ok();
    }
    
    @Override
    public void onDecline(@Nonnull Player player) {
    }
    
    @Override
    public void run() {
        // Running the code will act as "expired"
        final Player player = player();
        final Player invitee = invitee();
        
        strikethrough(
                player, () -> Chat.sendCenterMessage(player, "&eYour invite for %s&e has expired!".formatted(this.invitee.display().toString()))
        );
        
        strikethrough(
                invitee, () -> Chat.sendCenterMessage(invitee, "&eInvite from %s&e has expired!".formatted(this.player.display().toString()))
        );
        
        dispose();
    }
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        
        dispose();
    }
    
    @Override
    public void dispose() {
        // Remove by UUID
        playerInvites.get(player.getUuid()).removeIf(invite -> invite.uuid.equals(uuid));
    }
    
    public void doPerform(boolean b) {
        final Player player = player();
        final Player invitee = invitee();
        
        if (b) {
            final Response response = onAccept(invitee);
            
            if (response.isOk()) {
                strikethrough(player, () -> Chat.sendCenterMessage(player, "&2%s&2 accepted your invitation!".formatted(this.invitee.display().toString())));
                strikethrough(invitee, () -> Chat.sendCenterMessage(invitee, "&2Accepted %s&2's invitation!".formatted(this.player.display().toString())));
            }
            else {
                Message.error(invitee, "Cannot accept! {%s}".formatted(response.toString()));
            }
        }
        else {
            onDecline(invitee);
            
            strikethrough(player, () -> Chat.sendCenterMessage(player, "&4%s&4 declined your invitation!".formatted(this.invitee.display().toString())));
            strikethrough(invitee, () -> Chat.sendCenterMessage(invitee, "&4Declined %s&4's invitation!".formatted(this.player.display().toString())));
        }
        
        // Clean up
        cancel();
    }
    
    public static void send(@Nonnull Player player, @Nonnull Player invitee, @Nonnull Identifier identifier, @Nonnull Acceptable acceptable) {
        // Can't invite self
        if (player.equals(invitee)) {
            Message.error(player, "Cannot invite yourself!");
            return;
        }
        
        // Check if player has reached the invite limit
        final UUID playerUuid = player.getUniqueId();
        final Collection<PlayerInvite> playerInvites = PlayerInvite.playerInvites.get(playerUuid);
        
        if (playerInvites.size() >= inviteLimitPerInviter) {
            Message.error(player, "You have reached the invite limit, either cancel your invites or wait for them to expire!");
            return;
        }
        
        // Check for whether the invite with the same identifier already exists
        if (playerInvites.stream().anyMatch(invite -> invite.identifier.equals(identifier))) {
            Message.error(player, "You have already invite this player to %s!".formatted(identifier.prompt()));
            return;
        }
        
        // TODO -> Check for whether player can actually invite the target
        
        final PlayerInvite invite = new PlayerInvite(player, invitee, identifier) {
            @Override
            @Nonnull
            public Response onAccept(@Nonnull Player player) {
                return acceptable.onAccept(player);
            }
            
            @Override
            public void onDecline(@Nonnull Player player) {
                acceptable.onDecline(player);
            }
        };
        
        final PlayerProfile playerProfile = CF.getProfile(player);
        final PlayerProfile inviteeProfile = CF.getProfile(invitee);
        final String expireTimeFormatted = Tick.round(inviteExpireAfter);
        
        // Notify self
        strikethrough(
                player, () -> {
                    Chat.sendCenterMessage(player, "&eYou've invited %s&e to &6%s&e!".formatted(inviteeProfile.display().toString(), identifier.prompt()));
                    Chat.sendCenterMessage(player, "&8&oThey have %s to accept!".formatted(expireTimeFormatted));
                }
        );
        
        // Notify invitee
        strikethrough(
                invitee, () -> {
                    Chat.sendCenterMessage(invitee, "%s&e has invited you to &6%s&e!".formatted(playerProfile.display().toString(), identifier.prompt()));
                    Chat.sendCenterMessage(invitee, "&8&oYou have %s to accept!".formatted(expireTimeFormatted));
                    Chat.sendMessage(invitee, "");
                    
                    // Make buttons; I HECKING LOVE COMPONENTS!
                    final TextComponent.Builder component = Component.text()
                                                                     .append(
                                                                             Component.text("                   ")
                                                                                      .append(Component.text("✔ ", Color.SUCCESS).append(Component.text("ᴀᴄᴄᴇᴘᴛ", Color.SUCCESS, TextDecoration.BOLD)))
                                                                                      .hoverEvent(HoverEvent.showText(Component.text("Click to accept!", Color.SUCCESS, TextDecoration.ITALIC)))
                                                                                      .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/invite %s accept".formatted(invite.uuid)))
                                                                     )
                                                                     .append(
                                                                             Component.text("      ")
                                                                                      .append(Component.text("❌ ", Color.ERROR).append(Component.text("ᴅᴇᴄʟɪɴᴇ", Color.ERROR, TextDecoration.BOLD)))
                                                                                      .hoverEvent(HoverEvent.showText(Component.text("Click to decline!", Color.ERROR, TextDecoration.ITALIC)))
                                                                                      .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/invite %s decline".formatted(invite.uuid)))
                                                                     );
                    
                    invitee.sendMessage(component);
                }
        );
        
        // Actually store the invite
        PlayerInvite.playerInvites.put(playerUuid, invite);
    }
    
    @Nullable
    public static PlayerInvite byUuid(@Nonnull UUID uuid) {
        return playerInvites.values()
                            .stream()
                            .filter(invite -> invite.uuid.equals(uuid))
                            .findFirst()
                            .orElse(null);
    }
    
    private static void strikethrough(Player player, Runnable andThen) {
        Chat.sendMessage(player, CFUtils.strikethroughText(ChatColor.AQUA));
        andThen.run();
        Chat.sendMessage(player, CFUtils.strikethroughText(ChatColor.AQUA));
    }
    
}
