package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class CFCommand extends SimplePlayerCommand {

    private final PlayerRank rank;

    public CFCommand(@Nonnull String name, @Nonnull PlayerRank rank) {
        super(name);

        this.rank = rank;
    }

    @Nonnull
    public PlayerRank getRank() {
        return rank;
    }

    public <E extends Enum<E>> CFCommand enumCompleter(int index, @Nonnull Class<E> enumClass, @Nullable Archetype... ignore) {
        final List<E> list = Arrays.asList(enumClass.getEnumConstants());

        list.removeIf(e -> CFUtils.arrayContains(ignore, e));
        addCompleterValues(index, list);

        return this;
    }

    protected abstract void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank);

    @Override
    protected final void execute(Player player, String[] args) {
        final PlayerRank playerRank = PlayerRank.getRank(player);

        if (!playerRank.isOrHigher(rank)) {
            Notifier.Error.NOT_PERMISSIONS_NEED_RANK.send(player, rank.getPrefixWithFallback());
            return;
        }

        execute(player, new ArgumentList(args), rank);
    }
}
