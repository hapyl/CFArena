package me.hapyl.fight.cmds;

import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.TeamSelectGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

public class TeamCommand extends SimplePlayerCommand {
    public TeamCommand(String name) {
        super(name);

        addCompleterValues(1, "join", "leave");
        addCompleterValues(2, GameTeam.valuesStrings());
    }

    @Override
    protected void execute(Player player, String[] args) {
        // team - opens GUI
        // team toggle - toggles team mode
        // team join (team) - join certain team
        // team leave (team) - leave certain team

        if (args.length == 0) {
            new TeamSelectGUI(player);
            return;
        }

        final String arg0 = args[0].toLowerCase();

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

}
