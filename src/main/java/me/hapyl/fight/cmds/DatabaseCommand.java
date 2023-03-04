package me.hapyl.fight.cmds;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.StatisticEntry;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class DatabaseCommand extends SimplePlayerAdminCommand {
    public DatabaseCommand(String name) {
        super(name);
        setAliases("db");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final MongoCollection<Document> players = Main.getPlugin().getDatabase().getPlayers();

        Chat.sendMessage(player, "&aPlease wait...");

        if (args.length == 0) {
            players.find().forEach((Consumer<Document>) document -> {
                Chat.sendMessage(player, "&a" + document.toJson());
            });
            return;
        }

        if (args.length != 2) {
            return;
        }

        // database this.is.a.path.test.blah.blah
        final String path = args[0];
        final String value = args[1];

        final StatisticEntry entry = PlayerProfile.getProfile(player).getDatabase().getStatistics();

        if (value.equalsIgnoreCase("remove")) {
            entry.fetchPaths(path, Document::clear);
            Chat.sendMessage(player, "&aRemoved path &e" + path + " &afrom database!");
            return;
        }

        entry.fetchPaths(path, dc -> {
            dc.put("STATIC", value);
        });

        Chat.sendMessage(player, "&aSet path &e" + path + " &ato &e" + value + " &afor database!");
    }
}
