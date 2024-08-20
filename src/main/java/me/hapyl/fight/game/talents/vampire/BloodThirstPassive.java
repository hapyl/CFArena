package me.hapyl.fight.game.talents.vampire;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BloodThirstPassive extends PassiveTalent {
    public BloodThirstPassive(@Nonnull DatabaseKey key) {
        super(key, "Blood Thirst");

        setDescription("""
                &c;;Your health is constantly drained.
                
                Whenever you or your bats hit an opponent, you will gain a stack of &bblood&7, up to &b10&7 stacks.
                
                Drink the blood to &cincrease your damage&7 and &cheal yourself&7.
                
                &6;;Healing, damage boost, duration and cooldown are based on the number of stacks consumed.
                """
        );

        setItem(Material.REDSTONE);
    }
}
