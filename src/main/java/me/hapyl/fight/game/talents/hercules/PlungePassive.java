package me.hapyl.fight.game.talents.hercules;


import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class PlungePassive extends PassiveTalent {
    public PlungePassive(@Nonnull Key key) {
        super(key, "Plunge");

        setDescription("""
                While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.
                """
        );

        setItem(Material.COARSE_DIRT);
    }
}
