package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.entity.Player;

public class SyncDatabaseCommand extends SimplePlayerAdminCommand {
    public SyncDatabaseCommand(String name) {
        super(name);
        setDescription("Syncs the server database with remote.");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerProfile profile = CF.getProfile(player);
        final PlayerDatabase playerDatabase = profile.getDatabase();

        playerDatabase.save();
        Chat.sendMessage(player, "&aSynced database!");
    }
}
