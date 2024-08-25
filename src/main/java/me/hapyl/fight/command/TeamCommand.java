package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.TeamSelectGUI;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

public class TeamCommand extends SimplePlayerCommand {
    public TeamCommand(String name) {
        super(name);

        addCompleterValues(1, "join", "leave");
        addCompleterValues(2, GameTeam.valuesStrings());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cCannot modify team during a game!");
            return;
        }

        if (args.length == 0) {
            new TeamSelectGUI(player);
            return;
        }

        final String string = getArgument(args, 0).toString().toLowerCase();
        final GameTeam team = getArgument(args, 1).toEnum(GameTeam.class);

        if (team == null) {
            Chat.sendMessage(player, "&cInvalid team!");
            return;
        }

        if (string.equalsIgnoreCase("join")) {
            if (team.isFull()) {
                Notifier.error(player, "Cannot join {%s} team because it's full!".formatted(team.getName()));
                return;
            }

            if (!team.isAllowJoin()) {
                Notifier.error(player, "You cannot join this team directly!");
                return;
            }

            team.addEntry(Entry.of(player));
        }
    }

}
