package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
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
        gamePlayer.sendMessage("&aInterrupting in %d ticks!".formatted(delay));

        GameTask.runLater(() -> {
            gamePlayer.damage(0);
            gamePlayer.interrupt();
            gamePlayer.sendMessage("&cInterrupted!");
        }, delay);
    }
}
