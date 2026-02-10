package me.hapyl.fight.game.talents.moonwalker;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class MoonPassive extends PassiveTalent {

    @DisplayField public final int energyConversionRate = 5;
    @DisplayField public final int energyConversion = 25;

    @DisplayField public final double maxEnergy = 1000d;

    public MoonPassive(@Nonnull Key key) {
        super(key, "Moonlit Energy");

        setMaterial(Material.END_CRYSTAL);

        setDescription("""
                A type of alien energy that can power weapons.
                
                &6Moonlit Zone
                A concentrated zone filled with %s that can be gathered by &7&lSNEAKING&7 near it.
                """.formatted(Named.MOONLIT_ENERGY)
        );
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
