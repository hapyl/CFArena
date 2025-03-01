package me.hapyl.fight.command;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
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
        GamePlayer.getPlayerOptional(player)
                  .ifPresent(gamePlayer -> {
                      final Temper temper = args.get(0).toEnum(Temper.class);
                      final AttributeType type = args.get(1).toEnum(AttributeType.class);
                      final double value = args.get(2).toDouble();
                      final int duration = args.get(3).toInt(100);

                      if (temper == null) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid temper!");
                          return;
                      }

                      if (type == null) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid attribute!");
                          return;
                      }

                      if (value == 0.0d) {
                          gamePlayer.sendMessage(Message.ERROR, "Invalid attribute value!");
                          return;
                      }

                      temper.temper(gamePlayer, type, value, duration);
                      gamePlayer.sendMessage(Message.SUCCESS, "Tempered {%s} attribute by {%s} for {%s}s!".formatted(type.getName(), value, Tick.round(duration)));
                  });
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> completerSort(Temper.values(), args);
            case 2 -> completerSort(AttributeType.listNames(), args);
            default -> null;
        };
    }

}
