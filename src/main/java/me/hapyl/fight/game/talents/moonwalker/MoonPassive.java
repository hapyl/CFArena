package me.hapyl.fight.game.talents.moonwalker;


import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class MoonPassive extends PassiveTalent {

    @DisplayField public final double weaponEnergyConversion = 0.3d;
    @DisplayField public final double healingConversion = 0.1d;

    public MoonPassive(@Nonnull Key key) {
        super(key, "Moonlit Energy");

        setItem(Material.END_CRYSTAL);

        setDescription("""
                """
        );
    }
}
