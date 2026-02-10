package me.hapyl.fight.command;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.dot.Dot;
import me.hapyl.fight.game.dot.DotType;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DotCommand extends CFCommand {
    public DotCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        GamePlayer.getOptionalPlayer(player).ifPresent(gameplayer -> {
            final String stringKey = args.get(0).toString();
            final Key key = Key.ofStringOrNull(stringKey);
            
            if (key == null) {
                gameplayer.sendErrorMessage("Malformed key: {%s}".formatted(stringKey));
                return;
            }
            
            final Dot dotType = DotType.byKey(key);
            
            if (dotType == null) {
                gameplayer.sendErrorMessage("Invalid DoT: {%s}".formatted(stringKey));
                return;
            }
            
            // Check for clear first
            if (args.get(1).toString().equalsIgnoreCase("clear")) {
                if (!gameplayer.hasDot(dotType)) {
                    gameplayer.sendErrorMessage("Cannot clear {%s} DoT because you don't have it applied!".formatted(dotType));
                    return;
                }
                
                gameplayer.removeDot(dotType);
                gameplayer.sendSuccessMessage("Cleared {%s} DoT!".formatted(dotType));
            }
            else {
                final int stacks = args.get(1).toInt();
                
                if (stacks < 0) {
                    gameplayer.sendErrorMessage("Stacks cannot be negative!");
                    return;
                }
                
                gameplayer.addDotStacks(dotType, stacks);
                gameplayer.sendSuccessMessage("Applied {%s} stacks of {%s} DoT!".formatted(stacks, dotType));
            }
        });
    }
}
