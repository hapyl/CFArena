package me.hapyl.fight.command;

import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimpleAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;

public class UpdateParkourLeaderboardCommand extends SimpleAdminCommand {
    public UpdateParkourLeaderboardCommand(String name) {
        super(name);
        setUsage("/updateparkourleaderboard <parkour>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendInvalidUsageMessage(sender);
            return;
        }

        final ParkourCourse parkourCourse = Validate.getEnumValue(ParkourCourse.class, args[0]);
        if (parkourCourse == null) {
            Chat.sendMessage(sender, "&cInvalid parkour course!");
            return;
        }

        final ParkourLeaderboard leaderboard = parkourCourse.getParkour().getLeaderboard();
        if (leaderboard == null) {
            Chat.sendMessage(sender, "&cThis parkour course does not have a leaderboard!");
            return;
        }

        leaderboard.update();
        Chat.sendMessage(sender, "&aUpdated leaderboard!");
    }
}
