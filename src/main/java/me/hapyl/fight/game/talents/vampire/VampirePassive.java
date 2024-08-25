package me.hapyl.fight.game.talents.vampire;


import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class VampirePassive extends PassiveTalent {

    public VampirePassive(@Nonnull Key key) {
        super(key, "Blood Thirst");

        setDescription("""
                Dealing &cdamage&7 &4&ndrains&7 your &chealth&7, increasing the &cdamage&7 based on your &a&ncurrent&7 health.
                """);

        setItem(Material.OMINOUS_BOTTLE);
    }

}
