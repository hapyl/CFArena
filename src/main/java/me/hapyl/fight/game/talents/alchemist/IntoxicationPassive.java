package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class IntoxicationPassive extends PassiveTalent {
    
    @DisplayField(percentage = true) public final double curseDecrementPerOneCorrosion = 0.005d;
    
    @DisplayField public final int corrosionDecrementPeriod = 10;
    @DisplayField public final double corrosionDecrementPerSecond = 0.75d;
    
    public final double corrosionDecrement = corrosionDecrementPerSecond / (20d / corrosionDecrementPeriod);
    
    public IntoxicationPassive(@Nonnull Key key) {
        super(key, "Abyssal Corrosion");
        
        setDescription("""
                       &8&o;;Dealing with the Abyss has its consequences.
                       
                       Consuming too much abyssal energy increases your %1$s.
                       &8&o;;%2$s slowly dissipates over time.
                       
                       Having a high amount of %1$s hurts your &6body&7 and &esoul&7, but also decreases the time before %3$s becomes &4unstable&7.
                       """.formatted(Named.ABYSS_CORROSION, Named.ABYSS_CORROSION.getName(), Named.ABYSSAL_CURSE)
        );
        
        setMaterial(Material.DRAGON_BREATH);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
