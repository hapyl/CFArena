package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class EnergySupply extends Supply {
    
    private static final ParticleBuilder particle = ParticleBuilder.dustTransition(
            Color.fromRGB(2, 32, 79),
            Color.fromRGB(33, 55, 89),
            1
    );
    
    private final double rechargePercent = 0.1;
    
    EnergySupply() {
        super(Tick.fromMinutes(5), "Energy", "d884ed1950bb8b198ada8191684400bd6640e03710481c8122b780b9ed1bd98c");
        
        setDescription("""
                       Regenerates &b%.0f%%&7 of %s, regardless of resource type.
                       """.formatted(rechargePercent * 100, EnumResource.ENERGY));
    }
    
    @Override
    public void pickup(@Nonnull GamePlayer player) {
        final double cost = player.getUltimate().cost();
        final double toRegenerate = cost * rechargePercent;
        
        player.incrementEnergy(toRegenerate);
        
        // Fx
        player.sendTitle("&b\uD83C\uDF1F&9&l\uD83C\uDF1F&b\uD83C\uDF1F", "&a+&l%.0f".formatted(toRegenerate), 0, 15, 5);
        player.playWorldSound(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f);
    }
    
    @Override
    public void tick(@Nonnull SupplyInstance instance) {
        particle(instance, particle);
    }
}
