package me.hapyl.fight.game.talents.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class OverhealPassive extends PassiveTalent {
    
    @DisplayField(percentage = true) public final double damageIncreasePerOverheal = 0.15;
    @DisplayField public final double maxOverhealUse = 10;
    @DisplayField public final double maxOverhealDistance = 25;
    
    public OverhealPassive(@Nonnull Key key) {
        super(key, Named.OVERHEAL.getName());
        
        setDescription("""
                       When &ahealing&7 an ally who is already at &nfull health&7, the excess healing is converted into %1$s.
                       
                       When you or your allies deal &cdamage&7, it's increased by your %1$s.
                       &8&o;;The Overheal is consumed with the damage.
                       """.formatted(Named.OVERHEAL)
        );
        
        setMaterial(Material.GLISTERING_MELON_SLICE);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
