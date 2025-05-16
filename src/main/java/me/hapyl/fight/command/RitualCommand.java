package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.death.RitualCosmetic;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class RitualCommand extends CFCommand {
    public RitualCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final TypeConverter arg = args.get(0);
        final Particle particle = arg.toEnum(Particle.class);
        
        if (particle == null) {
            Message.error(player, "Illegal particle: " + arg);
            return;
        }
        
        if (!RitualCosmetic.RitualCosmeticEffect.isSupportedParticle(particle)) {
            Message.error(player, "{%s} is not a directional particle!".formatted(particle));
            return;
        }
        
        Message.success(player, "Displaying the effect with {%s} particle...".formatted(particle));
        new RitualCosmetic.RitualCosmeticEffect(new Display(player, player.getLocation()), particle).runTaskTimer(0, 1);
    }
    
    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(RitualCosmetic.RitualCosmeticEffect.supportedParticles(), args);
    }
}
