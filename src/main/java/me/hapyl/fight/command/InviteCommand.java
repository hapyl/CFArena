package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.invite.PlayerInvite;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class InviteCommand extends CFCommand {
    
    public InviteCommand(@Nonnull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // invite <uuid> <confirm, decline, cancel>
        final UUID uuid = BukkitUtils.getUUIDfromString(args.getString(0));
        final String argument = args.getString(1).toLowerCase();
        
        if (uuid == null) {
            Message.error(player, "Invalid uuid format!");
            return;
        }
        
        final PlayerInvite invite = PlayerInvite.byUuid(uuid);
        
        if (invite == null) {
            Message.error(player, "Invite doesn't exist or expired!");
            return;
        }
        
        // If self-call, only allow cancelling
        if (player.equals(invite.player())) {
            if (argument.equalsIgnoreCase("cancel")) {
                invite.cancel();
                Message.success(player, "Cancelled invite!");
                return;
            }
            
            Message.error(player, "Cannot accept nor decline own invite!");
        }
        else if (player.equals(invite.invitee())) {
            switch (argument) {
                case "accept" -> invite.doPerform(true);
                case "decline" -> invite.doPerform(false);
                default -> Message.error(player, "Invalid usage!");
            }
        }
        else {
            Message.error(player, "Illegal invite access!");
        }
    }
    
}
