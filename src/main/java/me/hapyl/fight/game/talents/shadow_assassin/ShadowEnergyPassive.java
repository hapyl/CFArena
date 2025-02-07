package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ShadowEnergyPassive extends PassiveTalent {
    public ShadowEnergyPassive(@Nonnull Key key) {
        super(key, "Shadow Energy");

        setDescription("""
                Accumulate %1$s while using talents in &9Stealth&7 mode.
                
                Spend %1$s to use empowered talents in &cFury&7 mode.
                """.formatted(Named.SHADOW_ENERGY)
        );

        setItem(Material.CHORUS_FRUIT);
    }
}
