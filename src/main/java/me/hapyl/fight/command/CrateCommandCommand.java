package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.crate.CrateChest;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;

public class CrateCommandCommand extends SimplePlayerCommand {
    public CrateCommandCommand(String name) {
        super(name);

        addCompleterValues(2, "give", "remove", "has");
        addCompleterValues(3, Crates.values());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            final CrateManager manager = Main.getPlugin().getCrateManager();
            final CrateChest closestCrate = manager.getClosest(player.getLocation());

            if (closestCrate == null) {
                Message.error(player, "There are no crate chests nearby!");
                return;
            }

            CF.getCrateManager().openCrate(player, closestCrate);
            return;
        }

        if (!PlayerRank.getRank(player).isOrHigher(PlayerRank.ADMIN)) {
            Message.Error.NOT_PERMISSIONS_NEED_RANK.send(player, PlayerRank.ADMIN);
            return;
        }

        // crate (player) (give, remove, has) (crate)
        if (args.length != 3) {
            Message.Error.NOT_ENOUGH_ARGUMENTS.send(player);
            return;
        }

        final Player target = Bukkit.getPlayer(getArgument(args, 0).toString());
        final String argument = getArgument(args, 1).toString().toLowerCase(Locale.ROOT);
        final Crates crate = getArgument(args, 2).toEnum(Crates.class);

        if (target == null) {
            Message.Error.PLAYER_NOT_ONLINE.send(player);
            return;
        }

        if (crate == null) {
            Message.Error.INVALID_ENUMERABLE_ARGUMENT.send(player, Arrays.toString(Crates.values()));
            return;
        }

        final PlayerDatabase database = PlayerDatabase.getDatabase(target);
        final CrateEntry crates = database.crateEntry;

        switch (argument) {
            case "give" -> {
                crates.addCrate(crate);
                Message.success(player, "Gave {} {} crate.", target.getName(), crate);
            }

            case "remove" -> {
                if (!crates.hasCrate(crate)) {
                    Message.error(player, "{} doesn't have any crates!", target.getName());
                    return;
                }

                crates.removeCrate(crate);
                Message.success(player, "Removed {} crate from {}.", crate, target.getName());
            }

            case "has" -> {
                final long count = crates.getCrates(crate);

                if (count > 0) {
                    Message.success(player, "{} has {} {} crates.", target.getName(), count, crate);
                }
                else {
                    Message.error(player, "{} doesn't have any {} crates!", target.getName(), crate);
                }
            }

            default -> Message.error(player, "Invalid usage!");
        }
    }
}
