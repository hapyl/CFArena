package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameEffectCommand extends SimplePlayerAdminCommand {
    public GameEffectCommand(String name) {
        super(name);

        setUsage("ge (Effect) (stop/int)");
        setAliases("ge");
    }

    @Override
    protected void execute(Player player, String[] args) {
        // ge (Effect) (stop/ticks)
        if (args.length == 2) {

            if (!Manager.current().isGameInProgress()) {
                Chat.sendMessage(player, "&cThis command is only available during the game!");
                return;
            }

            final Effects type = Validate.getEnumValue(Effects.class, args[0]);
            final int ticks = args[1].equalsIgnoreCase("stop") ? -1 : Validate.getInt(args[1]);

            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);
            if (type == null || gamePlayer == null) {
                Chat.sendMessage(player, "&cInvalid effect type.");
                return;
            }

            if (ticks <= -1) {
                if (gamePlayer.hasEffect(type)) {
                    Chat.sendMessage(player, "&cYou don't have %s effect applied!".formatted(type.getEffect().getName()));
                    return;
                }
                gamePlayer.removeEffect(type);
                Chat.sendMessage(player, "&aStopped %s effect!".formatted(type.getEffect().getName()));
                return;
            }

            gamePlayer.addEffect(type, ticks);
            Chat.sendMessage(player, "&aApplied %s effect for %st!".formatted(type.getEffect().getName(), ticks));

            return;
        }
        sendInvalidUsageMessage(player);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return completerSort(arrayToList(Effects.values()), args);
        }
        return null;
    }
}
