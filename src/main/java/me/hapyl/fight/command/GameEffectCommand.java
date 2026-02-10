package me.hapyl.fight.command;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.effect.ActiveEffect;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class GameEffectCommand extends CFCommand {
    public GameEffectCommand(String name) {
        super(name, PlayerRank.ADMIN);
        
        setUsage("ge (effect) (stop/int) [amplifier]");
        setAliases("ge");
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final GamePlayer gameplayer = GamePlayer.getExistingPlayer(player);
        
        if (gameplayer == null) {
            Message.error(player, "You must be in a game to use this command!");
            return;
        }
        
        if (args.length < 2) {
            Message.error(player, "Invalid usage! &e" + getUsage());
            return;
        }
        
        final Key key = Key.ofStringOrNull(args.get(0).toString());
        
        if (key == null) {
            Message.error(player, "Invalid key: {%s}!".formatted(args.get(0).toString()));
            return;
        }
        
        final Effect effect = EffectType.byKey(key);
        final int ticks = args.getString(1).equalsIgnoreCase("stop") ? -2 : args.getInt(1);
        final int amplifier = args.getInt(2);
        
        final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);
        
        if (effect == null || gamePlayer == null) {
            Message.error(player, "Invalid effect type: {%s}!".formatted(key));
            return;
        }
        
        final String effectName = effect.getName();
        
        
        if (ticks == -2) {
            if (!gamePlayer.hasEffect(effect)) {
                Message.error(player, "You don't have %s effect!".formatted(effectName));
                return;
            }
            
            gamePlayer.removeEffect(effect);
            Message.success(player, "Stopped %s effect!".formatted(effectName));
            return;
        }
        
        final ActiveEffect activeEffect = gamePlayer.getActiveEffect(effect);
        
        if (activeEffect != null && activeEffect.amplifier() > amplifier) {
            Message.error(player, "Unable to add the effect because you already have a stronger effect!");
            return;
        }
        
        gamePlayer.addEffect(effect, amplifier, ticks);
        Message.success(player, "Applied {%s} {%s} effect for {%s}t!".formatted(effectName, RomanNumber.toRoman(amplifier + 1), ticks));
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return completerSort(EffectType.keys(), args);
        }
        return null;
    }
}
