package me.hapyl.fight.gui;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.inventory.gui.StrictAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class RankClickAction implements StrictAction {

    private final PlayerRank rank;

    public RankClickAction(PlayerRank rank) {
        this.rank = rank;
    }

    public abstract void onClick(@Nonnull Player player);

    @Override
    public final void onLeftClick(@Nonnull Player player) {
        if (!checkRank(player)) {
            return;
        }

        onClick(player);
    }

    protected boolean checkRank(CommandSender sender) {
        if (!rank.isOrHigher(sender)) {
            Notifier.Error.NOT_PERMISSIONS_NEED_RANK.send(sender, rank.getPrefixWithFallback());
            return false;
        }

        return true;
    }

    @Nonnull
    public static RankClickAction of(@Nonnull PlayerRank rank, @Nonnull Consumer<Player> consumer) {
        return new RankClickAction(rank) {
            @Override
            public void onClick(@Nonnull Player player) {
                consumer.accept(player);
            }
        };
    }
}
