package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Color;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HealthSupply extends Supply {
    
    private static final ParticleBuilder particle = ParticleBuilder.dustTransition(Color.fromRGB(250, 170, 178), Color.fromRGB(209, 19, 38), 1);
    
    private final double healingPercent = 0.2;
    
    HealthSupply() {
        super(Tick.fromMinute(3), "Health", "466a5f7bcd3c1c225a9366eee6dfab1cc66a6bf7363fed087512e6ef47a1d");
        
        setDescription("""
                       Instantly heals for &c%.0f%%&7 of %s.
                       """.formatted(healingPercent * 100, AttributeType.MAX_HEALTH));
    }
    
    @Override
    public void pickup(@Nonnull GamePlayer player) {
        final double healing = player.getMaxHealth() * healingPercent;
        
        player.heal(healing);
        
        // Fx
        player.sendTitle("&c♥&4❤&c♥", "&a+&l%.0f".formatted(healing), 0, 15, 5);
        
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
        player.playWorldSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
    }
    
    @Override
    public void tick(@Nonnull SupplyInstance instance) {
        particle(instance, particle);
    }
    
}
