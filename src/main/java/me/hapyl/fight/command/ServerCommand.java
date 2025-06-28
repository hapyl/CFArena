package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.proxy.ServerType;
import me.hapyl.fight.proxy.TransferManager;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class ServerCommand extends CFCommand {
    public ServerCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
        
        addCompleterValues(1, "list", "goto");
        addCompleterValues(2, ServerType.values());
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // server - Display current server
        // server <list> - List all servers and their status
        // server <goto> <server> - Transfer to the server
        
        final TransferManager transferManager = CF.transferManager();
        
        if (args.length == 0) {
            transferManager.message(player, "&aYou're on &2%s&a server!".formatted(CF.getPlugin().thisServer()));
            return;
        }
        
        switch (args.getString(0).toLowerCase()) {
            case "list" -> {
                transferManager.message(player, "&2Listing all known servers:");
                
                ServerType.listServers()
                          .thenAccept(descriptions -> {
                              descriptions.sort(Comparator.comparingInt(d -> d.status().ordinal()));
                              
                              descriptions.forEach(description -> transferManager.message(player, description));
                          });
            }
            case "goto" -> {
                final ServerType serverType = ServerType.ofId(args.getString(1));
                
                if (serverType == null) {
                    transferManager.message(player, "&4Invalid server!");
                    return;
                }
                
                transferManager.transfer(player, serverType);
            }
            default -> Message.error(player, "Invalid usage!");
        }
        
    }
}
