package me.hapyl.fight.game.talents.inferno;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class DemonKindPassiveTalent extends PassiveTalent {

    public DemonKindPassiveTalent(@Nonnull Key key) {
        super(key, "Demonkind");

        setDescription("""
                As a kin of demons, you are fully adapted to the harshest environments and gain:
                
                &8● &7Immunity to &efire &7and &6lava&7.
                &8● &7Greatly increased %s and %s.
                """.formatted(AttributeType.EFFECT_RESISTANCE, AttributeType.KNOCKBACK_RESISTANCE));

        setMaterial(Material.ANCIENT_DEBRIS);
    }
}
