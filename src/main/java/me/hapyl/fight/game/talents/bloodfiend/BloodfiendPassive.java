package me.hapyl.fight.game.talents.bloodfiend;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BloodfiendPassive extends PassiveTalent {

    @DisplayField public final int biteDuration = Tick.fromSeconds(15);
    @DisplayField(percentage = true) public final double healthDeduction = -0.1;

    public BloodfiendPassive(@Nonnull Key key) {
        super(key, "Vampire's Bite");

        setDescription("""
                Biting your enemies inflicts &4succulence&7 for &b{biteDuration}&7.
                
                &4Bitten &cenemies&7 suffer health reduction and can be affected by your talents.
                """
        );

        setMaterial(Material.RED_DYE);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
