package me.hapyl.fight.cmds;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class SyncDatabaseCommand extends SimplePlayerAdminCommand {
    public SyncDatabaseCommand(String name) {
        super(name);
        setDescription("Syncs the database with the server.");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        final Database database = profile.getDatabase();

        database.sync();
        Chat.sendMessage(player, "&aSynced database!");
    }
}
