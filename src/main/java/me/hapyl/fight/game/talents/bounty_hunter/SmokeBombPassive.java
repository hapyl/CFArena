package me.hapyl.fight.game.talents.bounty_hunter;


import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SmokeBombPassive extends PassiveTalent {
    public SmokeBombPassive(@Nonnull Key key) {
        super(key, "Smoke Bomb");

        setDescription("""
                Whenever your &chealth&7 falls &nbelow&7 &c50%&7, you gain a &aSmoke Bomb&7.
                
                Throw it to create a &8smoke field&7 that &3blinds&7 everyone inside it and grant you a &bspeed boost&7.
                """
        );

        setItem(Material.ENDERMAN_SPAWN_EGG);
    }
}
