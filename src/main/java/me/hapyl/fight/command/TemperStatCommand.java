package me.hapyl.fight.command;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TemperStatCommand extends CFCommand {
    public TemperStatCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        GamePlayer.getOptionalPlayer(player)
                  .ifPresent(gamePlayer -> {
                      final AttributeType type = args.get(0).toEnum(AttributeType.class);
                      final ModifierType modifierType = args.get(1).toEnum(ModifierType.class);
                      final double value = args.get(2).toDouble();
                      final int duration = args.get(3).toInt(100);
                      
                      if (type == null) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid attribute!");
                          return;
                      }
                      
                      if (modifierType == null) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid modifier!");
                          return;
                      }
                      
                      if (value == 0.0d) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid attribute value!");
                          return;
                      }
                      
                      if (duration <= 0) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid duration!");
                          return;
                      }
                      
                      gamePlayer.getAttributes().addModifier(ModifierSource.COMMAND, duration, modifier -> modifier.of(type, modifierType, value));
                      gamePlayer.sendMessage(Message.SUCCESS, "Tempered {%s} attribute by {%.0f} ({%s}) for {%s}s!".formatted(type.getName(), value, modifierType, Tick.round(duration)));
                  });
    }
    
    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> completerSort(AttributeType.listNames(), args);
            case 2 -> completerSort(ModifierType.values(), args);
            default -> null;
        };
    }
    
}
