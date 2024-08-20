package me.hapyl.fight.game.talents.shaman;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ArcaneLinkPassive extends PassiveTalent {
    public ArcaneLinkPassive(@Nonnull DatabaseKey key) {
        super(key, "Arcane Linkage");

        setDescription("""
                Your &atotems&7 are linked by an invisible chain.
                
                &cEnemies&7 passing through a chain will take &cdamage&7.
                """
        );

        setItem(Material.CHAIN);
    }
}
