package me.hapyl.fight.game.talents.mage;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SoulHarvestPassive extends PassiveTalent {
    public SoulHarvestPassive(@Nonnull DatabaseKey key) {
        super(key, "Soul Harvest");

        setDescription("""
                Deal &bmelee &7damage to gain soul fragment as fuel for your &e&lSoul &e&lEater&7's range attacks.
                """
        );

        setItem(Material.SKELETON_SPAWN_EGG);
        setType(TalentType.IMPAIR);
    }
}
