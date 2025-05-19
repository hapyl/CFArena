package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class CritBoostSupply extends Supply {
    
    private static final ParticleBuilder particle = ParticleBuilder.dustTransition(
            Color.fromRGB(9, 95, 224),
            Color.fromRGB(92, 132, 191),
            1
    );
    
    private final double critChanceIncrease = 20;
    private final double critDamageIncrease = 40;
    private final int duration = Tick.fromSeconds(6);
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("critical_boost_supply"));
    
    CritBoostSupply() {
        super(0, "Critical Boost", "21a111ec1102c40c02d1d40f47e60938512393e9ddafefd5028d2d3a22bf518e");
        
        setDescription("""
                       Increases %s and %s by &a%.0f&7 and &a%.0f&7 respectively for &b%s.
                       """.formatted(AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE, critChanceIncrease, critDamageIncrease, CFUtils.formatTick(duration)));
    }
    
    @Override
    public void pickup(@Nonnull GamePlayer player) {
        player.getAttributes().addModifier(
                modifierSource, duration, modifier -> modifier
                        .of(AttributeType.CRIT_CHANCE, ModifierType.FLAT, critChanceIncrease)
                        .of(AttributeType.CRIT_DAMAGE, ModifierType.FLAT, critDamageIncrease)
        );
        
        // Fx
        player.sendTitle("&9☣&9&l☣&9☣", "&a+&l%.0f&a ☣ &8& &a+&l%.0f&a ☠".formatted(critChanceIncrease, critDamageIncrease), 0, 15, 5);
        
        player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 1.25f);
        player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 0.75f);
    }
    
    @Override
    public void tick(@Nonnull SupplyInstance instance) {
        particle(instance, particle);
    }
}
