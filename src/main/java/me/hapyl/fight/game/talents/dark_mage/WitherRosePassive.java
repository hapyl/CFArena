package me.hapyl.fight.game.talents.dark_mage;


import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class WitherRosePassive extends PassiveTalent {
    public WitherRosePassive(@Nonnull Key key) {
        super(key, "Wither Rose");

        setDescription("""
                Dealing &4damage&7 plants a %1$s&7 into the &cenemy&7.
                
                &nEach&7 stack of %1$s increases the &nduration&7 of your ultimate.
                """.formatted(Named.WITHER_ROSE)
        );

        setItem(Material.WITHER_ROSE);
    }
}
