package me.hapyl.fight.cmds;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.gui.ModeSelectGUI;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ModeCommand extends SimplePlayerCommand {

    public ModeCommand(String str) {
        super(str);
        setUsage("mode " + Arrays.toString(Modes.values()));
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            new ModeSelectGUI(player);
            return;
        }

        final Modes mode = Validate.getEnumValue(Modes.class, args[0]);

        if (mode == null) {
            sendInvalidUsageMessage(player);
            return;
        }

        Manager.current().setCurrentMode(mode);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(arrayToList(Modes.values()), args);
    }

}