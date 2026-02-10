package me.hapyl.fight.game.talents.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class DontAngerMePassive extends PassiveTalent {
    
    @DisplayField(percentage = true) public final double attackIncrease = 0.4;
    @DisplayField public final double speedIncrease = 25;
    @DisplayField public final double critChanceIncrease = 40;
    @DisplayField public final double defenseDecrease = -10_000_000;
    
    public DontAngerMePassive(@Nonnull Key key) {
        super(key, "Don't Anger Me");

        setDescription("""
                Taking &ncontinuous&7 &cdamage&7 within the set time window will trigger %s for &b3s&7.
                """.formatted(Named.BERSERK)
        );

        setMaterial(Material.FERMENTED_SPIDER_EYE);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
