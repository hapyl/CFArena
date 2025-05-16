package me.hapyl.fight.game.heroes.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class DragonSkinSpell extends MageSpell {
    
    @DisplayField private final double attackIncrease = 1.2d;
    @DisplayField private final double defenseIncrease = 0.7d;
    @DisplayField private final double speedReduction = -25;
    
    public DragonSkinSpell() {
        super(
                Key.ofString("mage_dragons_skin"),
                "Dragon's Skin",
                "Consume to gain &cincredible strength&7, but suffer %s and %s reduction.".formatted(
                        AttributeType.SPEED,
                        AttributeType.JUMP_STRENGTH
                ),
                Material.PHANTOM_MEMBRANE
        );
        
        setDurationSec(8);
    }
    
    @Override
    protected void useSpell(@Nonnull GamePlayer player) {
        player.getAttributes().addModifier(
                modifierSource, this, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackIncrease)
                        .of(AttributeType.DEFENSE, ModifierType.MULTIPLICATIVE, defenseIncrease)
                        .of(AttributeType.SPEED, ModifierType.FLAT, speedReduction)
                        .of(AttributeType.JUMP_STRENGTH, ModifierType.FLAT, -100)
        );
        
        // Fx
        player.spawnWorldParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.ENCHANTED_HIT, 40, 0.1, 0.1, 0.1, 1);
    }
    
}
