package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

public class CastSpellCommand extends SimplePlayerAdminCommand {
    public CastSpellCommand(String name) {
        super(name);

        setDescription("Allows casting a spell for debug purposes.");
        setUsage("/cast <spell> [-f]");

        addCompleterValues(1, Talents.values());
        addCompleterHandler(1, (player, arg) -> {
            final Talents talent = Validate.getEnumValue(Talents.class, arg);

            if (talent == null) {
                return "&c&nInvalid spell: {}!";
            }

            return "&a&nWill cast: " + talent.getName();
        });

        addCompleterHandler(2, (player, arg) -> {
            if (arg.equalsIgnoreCase("-f")) {
                return "&a&nSpell will be forced!";
            }

            return "&6&nShould force spell? &o(-f)";
        });

    }

    @Override
    protected void execute(Player player, String[] args) {
        // cast <spell> [-f]

        if (args.length < 1) {
            sendInvalidUsageMessage(player);
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cNo handle.");
            return;
        }

        final Talents talent = Validate.getEnumValue(Talents.class, args[0]);
        final boolean force = args.length > 1 && args[1].equalsIgnoreCase("-f");

        if (talent == null) {
            Chat.sendMessage(player, "&cInvalid spell!");
            return;
        }

        final Talent talentHandle = talent.getTalent();
        final Response response = force ? talentHandle.execute(gamePlayer) : talentHandle.execute0(gamePlayer);

        if (!response.isOk()) {
            Chat.sendMessage(player, "&c" + response.getReason());
        }
        else {
            Chat.sendMessage(player, "&aCasted spell!");
        }
    }
}
