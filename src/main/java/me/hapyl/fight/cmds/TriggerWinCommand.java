package me.hapyl.fight.cmds;

import me.hapyl.fight.game.AbstractGameInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TriggerWinCommand extends SimplePlayerAdminCommand {

    public TriggerWinCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] strings) {
        final AbstractGameInstance gameInstance = Manager.current().getCurrentGame();

        if (!(gameInstance instanceof GameInstance)) {
            Chat.sendMessage(player, "&cNo game instance.");
            return;
        }

        final boolean winCondition = gameInstance.getMode().testWinCondition((GameInstance) gameInstance);
        if (!winCondition) {
            Chat.sendMessage(player, "&cWin Condition is not met.");
            return;
        }
        gameInstance.checkWinCondition();
    }

}
