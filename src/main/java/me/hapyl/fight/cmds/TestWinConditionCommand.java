package me.hapyl.fight.cmds;

import me.hapyl.fight.game.AbstractGameInstance;
import me.hapyl.fight.game.FakeGamePlayer;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class TestWinConditionCommand extends SimplePlayerAdminCommand {
    public TestWinConditionCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final AbstractGameInstance gameInstance = Manager.current().getCurrentGame();
        if (!(gameInstance instanceof GameInstance)) {
            Chat.sendMessage(player, "&cNo game instance.");
            return;
        }

        if (args.length == 0) {
            final boolean isWin = gameInstance.getMode().testWinCondition((GameInstance) gameInstance);
            Chat.sendMessage(player, "isWin = " + isWin);
        }
        else {
            final GameTeam smallestTeam = GameTeam.getSmallestTeam();
            smallestTeam.addPlayer(new FakeGamePlayer());
            Chat.sendMessage(player, "&aAdded fake player to %s.", smallestTeam.getName());
        }
    }
}
