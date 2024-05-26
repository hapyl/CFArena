package me.hapyl.fight.game.talents.moonwalker;

import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

public class MoonPassive extends PassiveTalent {

    @DisplayField public final double weaponEnergyConversion = 0.3d;
    @DisplayField public final double healingConversion = 0.1d;

    public MoonPassive() {
        super("Moonlit Energy", Material.END_CRYSTAL);

        setDescription("""
                
                """);
    }
}
