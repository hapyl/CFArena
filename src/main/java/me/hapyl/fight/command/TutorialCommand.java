package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.game.help.HelpGeneral;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TutorialCommand extends SimplePlayerCommand {

    public TutorialCommand(String str) {
        super(str);

        setUsage("tutorial");
        setAliases("help");
    }

    @Override
    protected void execute(Player player, String[] strings) {
        new HelpGeneral(player);
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}