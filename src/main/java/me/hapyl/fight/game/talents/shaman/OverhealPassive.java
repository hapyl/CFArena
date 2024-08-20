package me.hapyl.fight.game.talents.shaman;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class OverhealPassive extends PassiveTalent {
    public OverhealPassive(@Nonnull DatabaseKey key) {
        super(key, Named.OVERHEAL.getName());

        setDescription("""
                When &ahealing&7 an &a&nally&7 who is already at &c&nfull&7 &c&nhealth&7, the excess &ahealing&7 is converted into %1$s.
                
                When &nyou&7 or &nyour&7 allies deal &cdamage&7, it's increased by your %1$s.
                &8;;The Overheal is consumed with the damage.
                """.formatted(Named.OVERHEAL)
        );

        setItem(Material.GLISTERING_MELON_SLICE);
    }
}
