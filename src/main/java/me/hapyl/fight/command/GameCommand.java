package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

public class GameCommand extends SimpleCommand {

    public GameCommand(String str) {
        super(str);
        setUsage("game (Start/Stop)");
        setDescription("Allows admins to control the game instance.");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        // game (start/stop/pause)
        if (args.length >= 1) {
            final Manager manager = Manager.current();
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "start" -> {
                    if (manager.isGameInProgress()) {
                        Chat.sendMessage(sender, "&cA game is already in progress, stop it first!");
                        return;
                    }

                    if (CF.environment().debug.isEnabled()) {
                        Debug.info("Creating a new debug instance...");
                    }
                    else {
                        Debug.info("Creating a new game instance...");
                    }

                    final boolean force = args.length >= 2 && args[1].equalsIgnoreCase("-f");
                    
                    manager.createNewGameInstance(() -> new GameInstance(manager.currentEnumType(), manager.currentEnumLevel()), force);
                }

                case "stop" -> {
                    if (!manager.isGameInProgress()) {
                        Chat.sendMessage(sender, "&cCouldn't find any game instances in progress.");
                        return;
                    }

                    if (!sender.isOp()) {
                        Chat.sendMessage(sender, "&4No permissions.");
                        return;
                    }

                    manager.stopCurrentGame();
                }

                default -> {
                    Chat.sendMessage(sender, "&cInvalid argument! " + this.getUsage());
                }
            }

            return;
        }
        Chat.sendMessage(sender, "&cNot enough arguments! " + this.getUsage());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}