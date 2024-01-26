package me.hapyl.fight.game.talents.archive.zealot;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

public class MaledictionVeil extends PassiveTalent {

    @DisplayField
    public final double radius = 2.5d;

    @DisplayField(percentage = true)
    public final double defenseIgnore = 0.2d;

    @DisplayField(percentage = true) private final double mendingDecrease = 0.5d;

    public final TemperInstance temperInstance = Temper.MALEDICTION_VEIL.newInstance()
            .decrease(AttributeType.VITALITY, mendingDecrease);

    public MaledictionVeil() {
        super("Malediction Veil", Material.PHANTOM_MEMBRANE);

        setDescription("""
                Emmit an aura that applies %1$s in a small AoE for {duration}.
                               
                %1$s:
                └ Decreases %2$s by &b{mendingDecrease}&7.
                └ &nYour&7 hits ignore &2{defenseIgnore}&7 %3$s.
                """, Named.CURSE_OF_GREED, AttributeType.VITALITY, AttributeType.DEFENSE);

        setDurationSec(6);
    }
}
