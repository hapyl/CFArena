package me.hapyl.fight.cmds;

import me.hapyl.fight.game.tutorial.Tutorial;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
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
        new Tutorial(player);
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}