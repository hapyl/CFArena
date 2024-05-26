package me.hapyl.fight.command;

import me.hapyl.fight.database.MongoUtils;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
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

        final PlayerDatabase playerDatabase = PlayerDatabase.getDatabase(player);
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
