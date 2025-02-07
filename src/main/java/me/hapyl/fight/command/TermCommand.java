package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.terminology.Term;
import me.hapyl.fight.terminology.TermCollectionGUI;
import me.hapyl.fight.terminology.TermGUI;
import me.hapyl.fight.terminology.Terms;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class TermCommand extends CFCommand {
    public TermCommand(@Nonnull String name, @Nonnull PlayerRank rank) {
        super(name, rank);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final String query = args.makeStringArray(0);

        if (query.isBlank()) {
            new TermCollectionGUI(player, null);
            return;
        }

        final List<Term> terms = Terms.byContext(query);

        if (terms.isEmpty()) {
            Notifier.ERROR.send(player, "Could not find any terms matching the query '{%s}'!".formatted(query));
        }
        else if (terms.size() == 1) {
            final Term term = terms.getFirst();

            new TermGUI(player, term);
        }
        else {
            new TermCollectionGUI(player, query);
        }
    }

}
