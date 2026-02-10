package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class RiptidePassive extends PassiveTalent {
    
    @DisplayField public final double damage = 5.0;
    @DisplayField public final int cooldown = 50;
    @DisplayField public final short numberOfHits = 3;
    @DisplayField public double xzSpread = 0.25;
    @DisplayField public double ySpread = 0.1;
    
    @DisplayField public final short meleeRiptideAmount = 100;
    @DisplayField public final short rangeRiptideAmount = 150;
    
    public RiptidePassive(@Nonnull Key key) {
        super(key, "Riptide");

        setDescription("""
                &bCritical&7 hits apply the %1$s effect to enemies.
                &8&o;;You apply more %2$s in Melee Stance.
                
                Dealing &bcritical&7 damage to enemies affected by %1$s triggers a &3Riptide Slash&7 that rapidly deals damage.
                &8&o;;Fully charged arrow is guaranteed to crit.
                """.formatted(Named.RIPTIDE, Named.RIPTIDE.getName())
        );

        setMaterial(Material.HEART_OF_THE_SEA);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
