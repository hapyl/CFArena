package me.hapyl.fight.game.talents.archer;

import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.terminology.Terms;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.checkerframework.checker.signature.qual.DotSeparatedIdentifiers;

import javax.annotation.Nonnull;

public class HawkeyePassive extends PassiveTalent {

    @DisplayField(percentage = true) public final double chance = 0.1d;
    @DotSeparatedIdentifiers public final double homingRadius = 2.0d;

    public HawkeyePassive(@Nonnull Key key) {
        super(key, "Hawkeye Arrow");

        setDescription("""
                Fully &ncharged&7 shots while &nsneaking&7 have &b{chance}&7 %s to fire a &3hawkeye&7 arrow that &nhomes&7 to nearby enemies.
                """.formatted(Terms.BASE_CHANCE)
        );

        setItem(Material.ENDER_EYE);
    }
}
