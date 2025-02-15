package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.store.Store;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class StoreCommand extends CFCommand {
    public StoreCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final String argument = args.get(0).toString();
        final Store store = CF.getPlugin().getStore();

        switch (argument.toLowerCase()) {
            case "respawn" -> {
                store.removeOffers(player);
                store.getOffers(player);

                Message.success(player, "Respawned store items!");
            }
            case "reset" -> {
                store.refreshOrders(player);

                Message.success(player, "Refresh orders!");
            }
            default -> Message.error(player, "Invalid usage!");
        }

    }
}
