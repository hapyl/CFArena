package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import org.bukkit.entity.Player;

public class TestWinConditionCommand extends SimplePlayerAdminCommand {
    public TestWinConditionCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final IGameInstance gameInstance = Manager.current().currentInstance();
        if (!(gameInstance instanceof GameInstance)) {
            Chat.sendMessage(player, "&cNo game instance.");
            return;
        }

        final boolean isWin = gameInstance.getMode().testWinCondition((GameInstance) gameInstance);
        Chat.sendMessage(player, "isWin = " + isWin);
    }
}
