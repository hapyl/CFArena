package me.hapyl.fight.cmds;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class InterruptCommand extends SimplePlayerAdminCommand {
    public InterruptCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cYou are not in a game!");
            return;
        }

        final int delay = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        gamePlayer.sendMessage("&aInterrupting in %d ticks!", delay);

        GameTask.runLater(() -> {
            gamePlayer.damage(0);
            gamePlayer.interrupt();
            gamePlayer.sendMessage("&cInterrupted!");
        }, delay);
    }
}
