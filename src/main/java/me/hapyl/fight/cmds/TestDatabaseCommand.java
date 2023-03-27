package me.hapyl.fight.cmds;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.MongoUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class TestDatabaseCommand extends SimplePlayerAdminCommand {
    public TestDatabaseCommand(String name) {
        super(name);

        setUsage("/testdatabase <path.path.path> [value]");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Database database = Database.getDatabase(player);

        if (args.length == 1) {
            final String str = MongoUtils.get(database.getConfig(), args[0], "");

            Chat.sendMessage(player, "&aValue: &e%s", str);
        }
        else {
            MongoUtils.set(database.getConfig(), args[0], args[1]);

            Chat.sendMessage(player, "&aSet value to &e%s&a!", args[1]);
            database.sync();
        }

    }
}
