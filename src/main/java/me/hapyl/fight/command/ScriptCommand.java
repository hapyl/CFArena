package me.hapyl.fight.command;

import me.hapyl.fight.Main;
import me.hapyl.fight.script.Script;
import me.hapyl.fight.script.ScriptManager;
import me.hapyl.fight.script.Scripts;
import me.hapyl.spigotutils.module.command.SimpleAdminCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ScriptCommand extends SimpleAdminCommand {
    public ScriptCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        final ScriptManager scriptManager = Main.getPlugin().getScriptManager();
        final String scriptName = getArgument(args, 0).toString();
        final Script script = Scripts.byId(scriptName);

        if (script == null) {
            sender.sendMessage(ChatColor.RED + "Invalid script: '%s'!".formatted(scriptName));
            return;
        }

        if (scriptManager.isRunning(script)) {
            sender.sendMessage(ChatColor.RED + "This script is already running!");
            return;
        }

        scriptManager.run(script);
        sender.sendMessage(ChatColor.GREEN + "Running script %s...".formatted(script));
    }

}
