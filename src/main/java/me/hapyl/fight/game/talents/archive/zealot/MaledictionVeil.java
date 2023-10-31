package me.hapyl.fight.game.talents.archive.zealot;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

public class MaledictionVeil extends PassiveTalent {

    @DisplayField(percentage = true)
    public final double defenseReduction = 0.1d;

    @DisplayField(percentage = true)
    public final double ferocityRate = 1.0d;

    public MaledictionVeil() {
        super("Malediction Veil", Material.PHANTOM_MEMBRANE);

        setDurationSec(12);

        setDescription("""
                Emmit an aura that applies %1$s in a small AoE for {duration}.
                                
                Enemies affected by %1$s will suffer %2$s reduction and %3$s is {ferocityRate} more effective.
                """, Named.CURSE_OF_GREED, AttributeType.DEFENSE, AttributeType.FEROCITY);
    }
}
