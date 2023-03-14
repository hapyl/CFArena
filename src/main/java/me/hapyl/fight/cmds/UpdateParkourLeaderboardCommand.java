package me.hapyl.fight.cmds;

import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

public class UpdateParkourLeaderboardCommand extends SimplePlayerAdminCommand {
    public UpdateParkourLeaderboardCommand(String name) {
        super(name);
        setUsage("/updateparkourleaderboard <parkour>");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            sendInvalidUsageMessage(player);
            return;
        }

        final ParkourCourse parkourCourse = Validate.getEnumValue(ParkourCourse.class, args[0]);
        if (parkourCourse == null) {
            Chat.sendMessage(player, "&cInvalid parkour course!");
            return;
        }

        final ParkourLeaderboard leaderboard = parkourCourse.getParkour().getLeaderboard();
        if (leaderboard == null) {
            Chat.sendMessage(player, "&cThis parkour course does not have a leaderboard!");
            return;
        }

        leaderboard.update();
        Chat.sendMessage(player, "&aUpdated leaderboard!");
    }
}
