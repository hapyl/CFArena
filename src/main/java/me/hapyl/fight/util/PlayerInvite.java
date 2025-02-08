package me.hapyl.fight.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.messagebuilder.MessageBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class PlayerInvite extends BukkitRunnable {

    private static final long inviteLifeTimeTicks = 600;
    private static final Map<UUID, PlayerInvite> invites = Maps.newHashMap();

    private final UUID uuid;
    private final String message;
    private final Player inviter;
    private final Map<Player, State> states;

    public PlayerInvite(@Nonnull Player inviter, @Nonnull Player invitee, @Nonnull String message) {
        this(inviter, Set.of(invitee), message);
    }

    public PlayerInvite(@Nonnull Player inviter, @Nonnull Set<Player> invitees, @Nonnull String message) {
        this.uuid = inviter.getUniqueId();
        this.inviter = inviter;
        this.message = message;

        this.states = Maps.newHashMap();
        this.states.put(inviter, State.ACCEPTED);

        invitees.forEach(player -> this.states.put(player, State.NEUTRAL));

        // Notify
        Message.success(inviter, "Invite has been sent!");

        final String playerMessage = "&b[&3✉&b] %s &ahas invited you to &2%s&a!".formatted(CF.getProfile(inviter)
                .getDisplay()
                .getNamePrefixed(), message);

        invitees.forEach(player -> {
            Chat.sendMessage(player, playerMessage);

            new MessageBuilder()
                    .append("                   ")
                    .append("&a&l✔ &nACCEPT")
                    .event(ClickEvent.Action.RUN_COMMAND, "/invite %s accept".formatted(uuid.toString()))
                    .event(HoverEvent.Action.SHOW_TEXT, "&aClick to accept!")
                    .append("      ")
                    .append("&c&l❌ &nDECLINE")
                    .event(ClickEvent.Action.RUN_COMMAND, "/invite %s decline".formatted(uuid.toString()))
                    .event(HoverEvent.Action.SHOW_TEXT, "&cClick to decline!")
                    .send(player);

            PlayerLib.plingNote(player, 2.0f);
        });

        // Put and schedule
        final PlayerInvite previousInvite = invites.put(uuid, this);

        if (previousInvite != null) {
            previousInvite.cancel();
        }

        runTaskLater(Main.getPlugin(), inviteLifeTimeTicks);
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    @Override
    public void run() {
        final PlayerInvite invite = invites.remove(uuid);

        if (!this.equals(invite)) { // was cleared before, don't care and should not have happened
            return;
        }

        // Notify players
        Message.error(inviter, "Your invite has expired!");
    }

    @Nonnull
    public UUID getUuid() {
        return uuid;
    }

    public boolean isAccepted() {
        for (State state : states.values()) {
            if (state != State.ACCEPTED) {
                return false;
            }
        }

        return true;
    }

    public boolean isDeclined() {
        for (State state : states.values()) {
            if (state == State.DECLINED) {
                return true;
            }
        }

        return false;
    }

    public void decline(@Nonnull Player player) { // One decline is enough to, well, decline
        if (inviter == player) { // Inviter cannot decline their own invite
            return;
        }

        onDecline();
        cleanup();
    }

    public void accept(@Nonnull Player player) {
        states.put(player, State.ACCEPTED);

        if (isAccepted()) {
            onAccept();
            cleanup();
        }
    }

    public abstract void onAccept();

    public abstract void onDecline();

    @Nonnull
    public Player getInviter() {
        return inviter;
    }

    @Nonnull
    public Set<Player> getInvitees() {
        final Set<Player> players = Sets.newHashSet(states.keySet());
        players.remove(inviter);

        return players;
    }

    @Nonnull
    public State getState(@Nonnull Player player) {
        return states.getOrDefault(player, State.INVALID);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final PlayerInvite invite = (PlayerInvite) object;
        return Objects.equals(uuid, invite.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public boolean isInvited(@Nonnull Player player) {
        return states.containsKey(player);
    }

    private void cleanup() {
        invites.remove(uuid, this);
        states.clear();
        cancel();
    }

    @Nullable
    public static PlayerInvite byUUID(UUID uuid) {
        return invites.get(uuid);
    }

    public enum State {
        /**
         * The player has accepted the invite.
         */
        ACCEPTED,
        /**
         * The player has neither accepted nor denied the invite.
         */
        NEUTRAL,
        /**
         * The player has denied the invite.
         */
        DECLINED,
        /**
         * The player was not invited. 😢
         */
        INVALID
    }
}
