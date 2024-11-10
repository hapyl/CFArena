package me.hapyl.fight.game.talents.pytaria;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ExcellencyPassive extends PassiveTalent {
    public ExcellencyPassive(@Nonnull Key key) {
        super(key, "Excellency");

        setDescription("""
                The less &chealth&7 Pytaria has, the more her %s and %s increases. But her %s significantly decreases.
                """.formatted(
                AttributeType.ATTACK,
                AttributeType.CRIT_CHANCE,
                AttributeType.DEFENSE
        ));

        setItem(Material.ROSE_BUSH);
    }
}
