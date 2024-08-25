package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.Notifier;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class MasteryCommand extends CFCommand {
    public MasteryCommand(@Nonnull String name, @Nonnull PlayerRank rank) {
        super(name, rank);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // mastery (player) (hero) (get,set) [value]

        final Player target = args.get(0).toPlayer();

        if (target == null) {
            Notifier.Error.PLAYER_NOT_ONLINE.send(player, args.get(0).toString());
            return;
        }

        final Hero hero = HeroRegistry.ofStringOrNull(args.get(1).toString());
        final String argument = args.get(2).toString();

        if (hero == null) {
            Notifier.error(player, "Invalid hero!");
            return;
        }

        final PlayerDatabase database = PlayerDatabase.getDatabase(target);
        if (argument.equalsIgnoreCase("get")) {
            final int lvl = database.masteryEntry.getLevel(hero);
            final long exp = database.masteryEntry.getExp(hero);

            Notifier.success(player, "{%s}'s mastery level for {%s} is: {%s} with a total of {%s} exp.".formatted(target.getName(), hero.getName(), lvl, exp));
        } else if (argument.equalsIgnoreCase("set")) {
            final long newExp = args.get(3).toLong(-1);

            if (newExp <= -1) {
                Notifier.error(player, "Invalid amount.");
                return;
            }

            database.masteryEntry.setExp(hero, newExp);

            Notifier.success(player, "Set {%s} mastery exp for {%s} to {%s}.".formatted(target.getName(), hero.getName(), newExp));
        } else {
            Notifier.error(player, "Invalid argument: " + argument);
        }

    }
}
