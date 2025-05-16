package me.hapyl.fight.game.talents.pytaria;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ExcellencyPassive extends PassiveTalent {

    @DisplayField public final double maxAttackIncrease = 1.3d;
    @DisplayField public final double maxCritChanceIncrease = 0.8d;
    @DisplayField public final double maxDefenseDecrease = -0.6d;

    public ExcellencyPassive(@Nonnull Key key) {
        super(key, "Excellency");

        setDescription("""
                The less &chealth&7 Pytaria has, the more her %s and %s increases.
                
                But her %s significantly decreases.
                """.formatted(
                AttributeType.ATTACK,
                AttributeType.CRIT_CHANCE,
                AttributeType.DEFENSE
        ));

        setMaterial(Material.ROSE_BUSH);
    }
}
