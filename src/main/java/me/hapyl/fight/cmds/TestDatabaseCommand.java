package me.hapyl.fight.cmds;

import me.hapyl.fight.database.MongoUtils;
import me.hapyl.fight.database.PlayerDatabase;
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

        final PlayerDatabase playerDatabase = PlayerDatabase.getDatabase(player);
        final String path = args[0];

        if (args.length == 1) {
            final String str = MongoUtils.get(playerDatabase.getConfig(), path, "");

            Chat.sendMessage(player, "&aValue: &e%s", str);
        }
        else {
            final String value = args[1];

            if (value.equalsIgnoreCase("null")) {
                MongoUtils.set(playerDatabase.getConfig(), path, null);
                Chat.sendMessage(player, "&aRemoved value!", value);
            }
            else {
                MongoUtils.set(playerDatabase.getConfig(), path, value);
                Chat.sendMessage(player, "&aSet value to &e%s&a!", value);
            }


            playerDatabase.sync();
        }

    }
}
