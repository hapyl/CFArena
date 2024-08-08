package me.hapyl.fight.command;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TriggerWinCommand extends SimplePlayerAdminCommand {

    public TriggerWinCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] strings) {
        final IGameInstance gameInstance = Manager.current().getCurrentGame();

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
