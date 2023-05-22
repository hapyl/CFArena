package me.hapyl.fight.cmds;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.PlayerAttributes;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PlayerAttributeCommand extends SimplePlayerAdminCommand {
    public PlayerAttributeCommand(String name) {
        super(name);

        setAliases("pa");
        addCompleterValues(1, AttributeType.values());
    }

    @Override
    protected void execute(Player player, String[] args) {
        // pa (TYPE) [newValue]
        final AttributeType type = Validate.getEnumValue(AttributeType.class, args[0]);
        final double newValue = getArgument(args, 1).toDouble();

        final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

        if (type == null) {
            Chat.sendMessage(player, "&4Error! &cInvalid type, try there: " + Arrays.toString(AttributeType.values()));
            return;
        }

        if (gamePlayer == null) {
            Chat.sendMessage(player, "&4Error! &cNo game instance.");
            return;
        }

        final PlayerAttributes attributes = gamePlayer.getAttributes();

        if (args.length > 1 && args[1].equalsIgnoreCase("reset")) {
            attributes.reset();

            Chat.sendMessage(player, "&aReset all attributes to base.");
            return;
        }

        // 0.0d means display the value
        if (newValue == 0.0d) {
            final double value = attributes.get(type);

            Chat.sendMessage(player, "&aYour %s is %s.", type.getName(), value);
            return;
        }

        attributes.add(type, newValue);
        Chat.sendMessage(player, "&aAdded %s to %s. Current value: %s.", newValue, type.getName(), attributes.get(type));
    }

}
