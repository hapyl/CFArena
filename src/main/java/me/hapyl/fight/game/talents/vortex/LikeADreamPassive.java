package me.hapyl.fight.game.talents.vortex;


import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class LikeADreamPassive extends PassiveTalent {
    public LikeADreamPassive(@Nonnull Key key) {
        super(key, "Like a Dream");

        setDescription("""
                &6Linking&7 to an &eAstral Star&7 grants you one stack of %1$s&7.
                
                Each %1$s&7 stack increases your &6astral&7 damage by &b15%%&7.
                
                &8;;Lose one stack after not gaining a stack for 5s.
                """.formatted(Named.ASTRAL_SPARK)
        );

        setItem(Material.RED_BED);
    }
}
