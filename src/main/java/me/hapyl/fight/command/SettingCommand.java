package me.hapyl.fight.command;

import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingCommand extends SimplePlayerCommand {

    public SettingCommand(String str) {
        super(str);

        setUsage("setting [Setting] (Value)");
        setAliases("settings", "options");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            new SettingsGUI(player);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.completerSort(Settings.values(), args);
    }

}