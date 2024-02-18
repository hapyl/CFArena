package me.hapyl.fight.game.talents.archive.swooper;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

public class SwooperPassive extends PassiveTalent {

    @DisplayField public final int sneakThreshold = Tick.fromSecond(5);
    @DisplayField public final double stealthDamageMultiplier = 2.0d;
    @DisplayField(suffix = "blocks") public final double maxNestStrayDistance = 1.0d;


    // FIXME (hapyl): 015, Feb 15:
    public SwooperPassive() {
        super("Sniper Nest", """
                While &nscoping&& for a &b{sneakThreshold}&7, create &6Sniper Nest&7 and enter %1$s.
                                
                &6%1$s:
                Become &9invisible&7 and &cincrease&7 your rifle damage.
                                
                &8;;Taking damage, moving or un-scoping clears this effect.
                """.formatted(Named.REFRACTION), Material.FERMENTED_SPIDER_EYE);

        setCooldownSec(3);
    }
}
