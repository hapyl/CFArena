package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.crate.CrateLocation;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
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
            final CrateLocation closestCrate = manager.getClosest(player.getLocation());

            if (closestCrate == null) {
                Notifier.error(player, "There are no crate chests nearby!");
                return;
            }

            CF.getCrateManager().openCrate(player, closestCrate);
            return;
        }

        if (!PlayerRank.getRank(player).isOrHigher(PlayerRank.ADMIN)) {
            Notifier.Error.NOT_PERMISSIONS_NEED_RANK.send(player, PlayerRank.ADMIN);
            return;
        }

        // crate (player) (give, remove, has) (crate)
        if (args.length != 3) {
            Notifier.Error.NOT_ENOUGH_ARGUMENTS.send(player);
            return;
        }

        final Player target = Bukkit.getPlayer(getArgument(args, 0).toString());
        final String argument = getArgument(args, 1).toString().toLowerCase(Locale.ROOT);
        final Crates crate = getArgument(args, 2).toEnum(Crates.class);

        if (target == null) {
            Notifier.Error.PLAYER_NOT_ONLINE.send(player);
            return;
        }

        if (crate == null) {
            Notifier.Error.INVALID_ENUMERABLE_ARGUMENT.send(player, Arrays.toString(Crates.values()));
            return;
        }

        final PlayerDatabase database = PlayerDatabase.getDatabase(target);
        final CrateEntry crates = database.crateEntry;

        switch (argument) {
            case "give" -> {
                crates.addCrate(crate);
                Notifier.success(player, "Gave {} {} crate.", target.getName(), crate);
            }

            case "remove" -> {
                if (!crates.hasCrate(crate)) {
                    Notifier.error(player, "{} doesn't have any crates!", target.getName());
                    return;
                }

                crates.removeCrate(crate);
                Notifier.success(player, "Removed {} crate from {}.", crate, target.getName());
            }

            case "has" -> {
                final long count = crates.getCrates(crate);

                if (count > 0) {
                    Notifier.success(player, "{} has {} {} crates.", target.getName(), count, crate);
                }
                else {
                    Notifier.error(player, "{} doesn't have any {} crates!", target.getName(), crate);
                }
            }

            default -> Notifier.error(player, "Invalid usage!");
        }
    }
}
