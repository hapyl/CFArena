package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class MagneticAttractionPassive extends PassiveTalent {
    
    @DisplayField public final short maxIron = 20; // w 10
    @DisplayField public final short startIron = maxIron >> 1;
    @DisplayField public final int rechargeRate = 50; // w 60
    
    public MagneticAttractionPassive(@Nonnull Key key) {
        super(key, "Magnetic Attraction");
        
        setDescription("""
                       You may possess up to &b{maxIron} &fIron&7 that replenishes passively.
                       &8&o;;There are also other ways to obtain Iron.
                       
                       Use &fIron&7 to create, upgrade, and repair your constructs.
                       &8&o;;You will start with {startIron} Iron.
                       """
        );
        
        setMaterial(Material.IRON_INGOT);
    }
}
