package me.hapyl.fight.cmds;

import com.mongodb.client.MongoCollection;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.DatabaseMongo;
import me.hapyl.fight.game.Debugger;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.List;

public class DatabaseCommand extends SimplePlayerAdminCommand {
    public DatabaseCommand(String name) {
        super(name);
        setAliases("db");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final DatabaseMongo database = Main.getPlugin().getDatabase();
        final MongoCollection<Document> players = database.getPlayers();

        final String pathRaw = args[0];
        final String[] path = args[0].split("\\.");
        final String key = args[1];
        final String value = args[2];

        Runnables.runAsync(() -> {
            Document document = players.find(new Document("test", "test")).first();

            if (document == null) {
                document = new Document("test", "test");
                players.insertOne(document);
            }

            final String dbValue = document.getEmbedded(List.of(path, key), "");

            Debugger.keepLog(dbValue);

            Document finalDocument = document;
            players.updateOne(finalDocument, new Document("$set", new Document(pathRaw + "." + key, value)));
        });

        Chat.sendMessage(player, "&aUpdated for %s!", pathRaw);

        //Chat.sendMessage(player, "&aPlease wait...");
        //
        //if (args.length == 0) {
        //    players.find().forEach((Consumer<Document>) document -> {
        //        Chat.sendMessage(player, "&a" + document.toJson());
        //    });
        //    return;
        //}
        //
        //if (args.length != 2) {
        //    return;
        //}
        //
        //// database this.is.a.path.test.blah.blah
        //final String path = args[0];
        //final String value = args[1];
        //
        //final StatisticEntry entry = PlayerProfile.getProfile(player).getDatabase().getStatistics();
        //
        //if (value.equalsIgnoreCase("remove")) {
        //    entry.fetchPaths(path, Document::clear);
        //    Chat.sendMessage(player, "&aRemoved path &e" + path + " &afrom database!");
        //    return;
        //}
        //
        //entry.fetchPaths(path, dc -> {
        //    dc.put("STATIC", value);
        //});
        //
        //Chat.sendMessage(player, "&aSet path &e" + path + " &ato &e" + value + " &afor database!");
    }
}
