package me.hapyl.fight.game.talents.nightmare;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class InTheShadowsPassive extends PassiveTalent {

    @DisplayField public final short moodyLight = 7;
    @DisplayField public final int buffDuration = 60;
    
    @DisplayField(percentage = true) public final double attackIncrease = 0.5;
    @DisplayField public final double speedIncrease = 25;

    public InTheShadowsPassive(@Nonnull Key key) {
        super(key, "In the Shadows");

        setDescription("""
                While in &8moody&7 light, your %s&7 and %s&7 increases.
                """.formatted(AttributeType.ATTACK, AttributeType.SPEED)
        );

        setMaterial(Material.DRIED_KELP);
    }
}
