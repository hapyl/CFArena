package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.gui.ModeSelectGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ModeCommand extends SimplePlayerCommand {

    public ModeCommand(String str) {
        super(str);
        setUsage("mode " + EnumGameType.getSelectableGameTypes());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cCannot use now.");
            return;
        }

        if (args.length == 0) {
            new ModeSelectGUI(player);
            return;
        }

        final EnumGameType mode = Enums.byName(EnumGameType.class, args[0]);

        if (mode == null) {
            sendInvalidUsageMessage(player);
            return;
        }

        mode.select(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(EnumGameType.getSelectableGameTypes(), args);
    }

}