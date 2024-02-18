package me.hapyl.fight.command;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.NoProfileException;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class SyncDatabaseCommand extends SimplePlayerAdminCommand {
    public SyncDatabaseCommand(String name) {
        super(name);
        setDescription("Syncs the server database with remote.");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            throw new NoProfileException(player);
        }

        final PlayerDatabase playerDatabase = profile.getDatabase();

        playerDatabase.save();
        Chat.sendMessage(player, "&aSynced database!");
    }
}
