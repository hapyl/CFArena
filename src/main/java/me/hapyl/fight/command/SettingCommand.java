package me.hapyl.fight.command;

import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.util.Validate;
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
        else {

            final Settings setting = Validate.getEnumValue(Settings.class, args[0]);
            if (setting == null) {
                Chat.sendMessage(player, "&cInvalid setting \"%s\"!".formatted(args[0]));
                return;
            }

            final boolean value = args.length >= 2 ? args[1].equalsIgnoreCase("true") : !setting.isEnabled(player);
            setting.setEnabled(player, value);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.completerSort(Settings.values(), args);
    }

}