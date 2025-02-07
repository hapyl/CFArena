package me.hapyl.fight.game.talents.alchemist;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class IntoxicationPassive extends PassiveTalent {
    public IntoxicationPassive(@Nonnull Key key) {
        super(key, "Intoxication");

        setDescription("""
                Drinking potions will increase &eIntoxication &7level that will decrease constantly.
                
                Having high &eIntoxication&7 levels isn't good for your body!
                """
        );

        setItem(Material.DRAGON_BREATH);
    }
}
