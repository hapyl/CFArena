package me.hapyl.fight.game.talents.vampire;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class VampirePassive extends PassiveTalent {

    public VampirePassive(@Nonnull Key key) {
        super(key, "Blood Thirst");

        setDescription("""
                Increases your &cdamage&7 based on the amount of %s&7.
                
                &8&o;;When healed, the debt is restored first before any health recovery.
                """.formatted(Named.BLOOD_DEBT));

        setItem(Material.REDSTONE);
    }

}
