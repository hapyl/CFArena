package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.Manager;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class StartCountdownCommand extends CFCommand {
    public StartCountdownCommand(@Nonnull String name) {
        super(name, PlayerRank.DEFAULT);

        setCooldownTick(60);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        Manager.current().doStartOrCancelCountdown(player);
    }
}
