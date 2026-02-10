package me.hapyl.fight.game.talents.bounty_hunter;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BountyHunterPassive extends PassiveTalent {
    
    @DisplayField public final double backstabDamageMultiplier = 2.0;
    @DisplayField public final double backstabDotThreshold = -0.7;
    @DisplayField public final double critChanceIncrease = 20;
    
    public BountyHunterPassive(@Nonnull Key key) {
        super(key, "Backstab");
        
        setDescription("""
                       Damage dealt from behind is:
                        &8├&7 Multiplied by &cx{backstabDamageMultiplier}&7.
                        &8└&7 Has increased %s.
                       """.formatted(AttributeType.CRIT_CHANCE));
        
        setMaterial(Material.NETHERITE_SWORD);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
