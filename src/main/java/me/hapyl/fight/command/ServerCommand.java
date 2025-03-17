package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.proxy.ServerType;
import me.hapyl.fight.proxy.TransferManager;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ServerCommand extends CFCommand {
    public ServerCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);

        addCompleterValues(0, ServerType.values());
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final ServerType serverType = args.get(0).toEnum(ServerType.class);

        if (serverType == null) {
            Message.success(player, "You are on {%s} server!".formatted(CF.getPlugin().serverType()));
            return;
        }

        TransferManager.transfer(player, serverType);
    }
}
