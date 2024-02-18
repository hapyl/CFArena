package me.hapyl.fight.command;

import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.PlayerInvite;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InviteCommand extends SimplePlayerCommand {

    public InviteCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 2) {
            final String stringUUID = getArgument(args, 0).toString();
            final UUID uuid = CFUtils.getUUIDfromString(stringUUID);

            if (uuid == null) {
                Message.error(player, "Invalid UUID!");
                return;
            }

            final PlayerInvite invite = PlayerInvite.byUUID(uuid);

            if (invite == null) {
                Message.error(player, "Could not find the invite you're trying to interact with!");
                return;
            }

            if (!invite.isInvited(player)) {
                Message.error(player, "You are not invited!");
                return;
            }

            final String argument = getArgument(args, 1).toString().toLowerCase();

            switch (argument) {
                case "accept" -> invite.accept(player);
                case "decline" -> invite.decline(player);
                default -> {
                    Message.error(player, "Invalid argument, expected either 'accept' or 'decline', got '%s'!".formatted(argument));
                }
            }
            return;
        }

        Message.error(player, "Invalid usage!");
    }
}
