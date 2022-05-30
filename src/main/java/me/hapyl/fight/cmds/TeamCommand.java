package me.hapyl.fight.cmds;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.TeamSelectGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TeamCommand extends SimplePlayerAdminCommand {
    public TeamCommand(String name) {
        super(name);
        addCompleterValues(1, "toggle", "join", "leave");
        addCompleterValues(2, GameTeam.valuesStrings());
    }

    @Override
    protected void execute(Player player, String[] args) {
        // team - opens GUI
        // team toggle - toggles team mode
        // team join (team) - join certain team
        // team leave (team) - leave certain team

        if (args.length == 0) {
            if (validateTeamMode(player)) {
                return;
            }
            new TeamSelectGUI(player);
            return;
        }

        final String arg0 = args[0].toLowerCase();

        if (args.length == 1 && arg0.equalsIgnoreCase("toggle")) {
            if (!player.isOp()) {
                Chat.sendMessage(player, "&cNo permissions.");
                return;
            }
            Manager.current().toggleTeamMode();
            final boolean teamMode = Manager.current().isTeamMode();

            if (teamMode) {
                Chat.broadcast("&a&lTeam mode enabled!");
                Chat.broadcast("&aUse &e/team &ato select your team.");
                PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
            }
            else {
                Chat.broadcast("&cTeam mode disabled.");
                GameTeam.clearAll();
            }
            return;
        }

        if (validateTeamMode(player)) {
            return;
        }

        final GameTeam team = Validate.getEnumValue(GameTeam.class, args[1]);
        if (team == null) {
            Chat.sendMessage(player, "&cCould not find team '%s'!", args[1]);
            return;
        }

        if (arg0.equalsIgnoreCase("join")) {
            if (team.addToTeam(player)) {
                Chat.sendMessage(player, "&aJoined %s team.", team.getName());
            }
            else {
                Chat.sendMessage(player, "&cCannot join %s team because it's full!", team.getName());
            }
        }
    }

    private boolean validateTeamMode(Player player) {
        final boolean teamMode = Manager.current().isTeamMode();
        if (!teamMode) {
            Chat.sendMessage(player, "&cTeam mode is not enabled!");
        }
        return !teamMode;
    }

}
