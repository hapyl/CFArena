package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.MongoUtils;
import me.hapyl.fight.database.PlayerDatabase;
import org.bukkit.entity.Player;

public class TestDatabaseCommand extends SimplePlayerAdminCommand {
    public TestDatabaseCommand(String name) {
        super(name);

        setUsage("/testdatabase <string.string.string> [value]");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            sendInvalidUsageMessage(player);
            return;
        }

        final PlayerDatabase playerDatabase = CF.getDatabase(player);
        final String path = args[0];

        if (args.length == 1) {
            final String str = String.valueOf(MongoUtils.get(playerDatabase.getDocument(), path, null));

            Chat.sendMessage(player, "&aValue: &e%s".formatted(str));
        }
        else {
            final String value = args[1];

            if (value.equalsIgnoreCase("null")) {
                MongoUtils.set(playerDatabase.getDocument(), path, null);
                Chat.sendMessage(player, "&aRemoved value!");
            }
            else {
                MongoUtils.set(playerDatabase.getDocument(), path, value);
                Chat.sendMessage(player, "&aSet value to &e%s&a!".formatted(value));
            }


            playerDatabase.save();
        }

    }
}
