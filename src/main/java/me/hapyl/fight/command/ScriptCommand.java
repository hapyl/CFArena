package me.hapyl.fight.command;

import me.hapyl.fight.Main;
import me.hapyl.fight.script.ScriptManager;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimpleAdminCommand;
import org.bukkit.command.CommandSender;

public class ScriptCommand extends SimpleAdminCommand {
    public ScriptCommand(String name) {
        super(name);

        addCompleterValues(1, "reload", "run");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        final String argument = getArgument(args, 0).toString();
        final ScriptManager manager = Main.getPlugin().getScriptManager();

        // script reload
        // script run <script>

        switch (argument.toLowerCase()) {
            case "reload" -> {
                Chat.sendMessage(sender, "&aReloading...");
                manager.reload();
            }
            case "run" -> {
                final String id = getArgument(args, 1).toString();

                manager.run(id);
            }
        }
    }
}
