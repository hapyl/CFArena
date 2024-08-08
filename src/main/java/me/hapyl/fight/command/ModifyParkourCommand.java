package me.hapyl.fight.command;

import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.parkour.ParkourDatabase;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimpleAdminCommand;
import org.bson.Document;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ModifyParkourCommand extends SimpleAdminCommand {

    public ModifyParkourCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        // modifyParkour (parkour) (entry) (time)

        final ParkourCourse parkourCourse = getArgument(args, 0).toEnum(ParkourCourse.class);

        if (parkourCourse == null) {
            Chat.sendMessage(sender, "&cInvalid parkour!");
            return;
        }

        final CFParkour parkour = parkourCourse.getParkour();
        final ParkourDatabase database = parkour.getDatabase();

        UUID entry;

        try {
            entry = UUID.fromString(getArgument(args, 1).toString());
        } catch (Exception e) {
            Chat.sendMessage(sender, "&cInvalid UUID format!");
            return;
        }

        final Document document = database.getPlayer(entry);

        if (document == null) {
            Chat.sendMessage(sender, "&cInvalid entry!");
            return;
        }

        final long time = getArgument(args, 2).toLong();

        if (time < 0) {
            Chat.sendMessage(sender, "&cTime cannot be negative!");
            return;
        }

        document.append("time", time).append("is_dirty", true);
        database.write("players." + entry, document, then -> {
            parkour.updateLeaderboardIfExists();
        });

        final ParkourLeaderboard leaderboard = parkour.getLeaderboard();

        if (leaderboard != null) {
            leaderboard.update();
        }

        Chat.sendMessage(sender, "&aModified entry!");
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return completerSort(ParkourCourse.values(), args);
        }

        final ParkourCourse parkourCourse = getArgument(args, 0).toEnum(ParkourCourse.class);

        if (parkourCourse == null) {
            return null;
        }

        if (args.length != 2) {
            return null;
        }

        return completerSort(parkourCourse.getParkour().getDatabase().getPlayers().keySet(), args);
    }
}
