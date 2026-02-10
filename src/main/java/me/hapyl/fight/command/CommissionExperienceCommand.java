package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CommissionEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CommissionExperienceCommand extends CFCommand {
    public CommissionExperienceCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);

        addCompleterValues(2, "has", "reset", "set", "give", "remove");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // commissionExperience (player) (has/reset/set/give/remove) [exp]
        final Player target = args.get(0).toPlayer();
        final String operation = args.get(1).toString();
        final long amount = args.get(2).toLong();

        if (target == null) {
            Message.error(player, "This player is not online!");
            return;
        }

        final PlayerDatabase database = CF.getDatabase(target);
        final CommissionEntry entry = database.commissionEntry;

        switch (operation.toLowerCase()) {
            case "has" -> {
                final long exp = entry.exp();

                Message.success(player, "{%s} has {%,d} (level %s) commission experience!".formatted(target.getName(), exp, entry.level()));
            }
            case "reset" -> {
                entry.exp(0);

                Message.success(player, "Reset {%s}'s commission experience!".formatted(target.getName()));
            }
            case "set" -> {
                entry.exp(amount);

                Message.success(player, "Set {%s}'s commission experience to {%s}!".formatted(target.getName(), amount));
            }
            case "give" -> {
                entry.incrementExp(amount);

                Message.success(player, "Gave {%s} {%s} commission experience!".formatted(target.getName(), amount));
            }
            case "remove" -> {
                entry.decrementExp(amount);

                Message.success(player, "Took {%s} commission experience from {%s}!".formatted(amount, target.getName()));
            }
        }
    }
}
