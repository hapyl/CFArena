package me.hapyl.fight.command;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class CFCommand extends SimplePlayerCommand {

    private final PlayerRank rank;

    public CFCommand(@Nonnull String name, @Nonnull PlayerRank rank) {
        super(name);

        this.rank = rank;
    }

    protected abstract void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank);

    @Override
    protected final void execute(Player player, String[] args) {
        final PlayerRank playerRank = PlayerRank.getRank(player);

        if (!playerRank.isOrHigher(rank)) {
            Message.Error.NOT_PERMISSIONS_NEED_RANK.send(player, rank.getPrefixWithFallback());
            return;
        }

        execute(player, new ArgumentList(args), rank);
    }
}
