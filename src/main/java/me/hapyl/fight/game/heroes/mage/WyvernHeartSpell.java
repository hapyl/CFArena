package me.hapyl.fight.game.heroes.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class WyvernHeartSpell extends MageSpell {
    
    @DisplayField(scale = 100, suffix = "% of Max Health") private final double healingAmount = 0.5d;
    @DisplayField(scale = 100) private final double attackDecrease = -0.98d;
    @DisplayField private final double speedIncrease = 50;
    
    public WyvernHeartSpell() {
        super(
                Key.ofString("mage_heart_of_wyvern"),
                "Heart of Wyvern",
                "Consume to &cheal&7 yourself, gain &bspeed&7 boost but suffer %s reduction.".formatted(AttributeType.ATTACK),
                Material.FERMENTED_SPIDER_EYE
        );
        
        setDurationSec(10);
    }
    
    @Override
    protected void useSpell(@Nonnull GamePlayer player) {
        final int duration = getDuration();
        
        player.getAttributes().addModifier(
                modifierSource, duration, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackDecrease)
                        .of(AttributeType.SPEED, ModifierType.FLAT, speedIncrease)
        );
        
        player.healRelativeToMaxHealth(healingAmount);
    }
}
