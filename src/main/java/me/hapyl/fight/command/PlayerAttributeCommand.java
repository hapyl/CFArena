package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerAttributeCommand extends CFCommand {

    public PlayerAttributeCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);

        addCompleterValues(1, AttributeType.values());
        addCompleterValues(1, "reset");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final AttributeType type = args.get(0).toEnum(AttributeType.class);
        final double newValue = args.get(1).toDouble();

        GamePlayer.getPlayerOptional(player)
                  .ifPresent(gamePlayer -> {
                      final EntityAttributes attributes = gamePlayer.getAttributes();

                      // Reset
                      if (args.get(0).toString().equalsIgnoreCase("reset")) {
                          attributes.reset();
                          gamePlayer.updateAttributes();

                          gamePlayer.sendMessage(Message.SUCCESS, "Reset all attributes to base.");
                          return;
                      }

                      if (type == null) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid type, valid types: " + AttributeType.listNames());
                          return;
                      }

                      // 0.0d means display the value
                      if (newValue == 0.0d) {
                          final double value = attributes.get(type);

                          gamePlayer.sendMessage(Message.SUCCESS, "Your {%s} attribute is {%.1f} &8(%.0f)&f!".formatted(type.getName(), value, type.scaleUp(value)));
                          return;
                      }

                      attributes.set(type, newValue - attributes.getBase(type));
                      gamePlayer.sendMessage(Message.SUCCESS, "Set your {%s} attribute to {%s}!".formatted(type.getName(), attributes.get(type)));
                  });
    }


}
