package me.hapyl.fight.game.talents.archive.swooper;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

public class SwooperPassive extends PassiveTalent {

    @DisplayField public final int standStillTime = Tick.fromSecond(5);
    @DisplayField public final double stealthDamageMultiplier = 2.0d;

    public SwooperPassive() {
        super("You Can't See Me", """
                While &nscoping&7 &land&7 &nstanding&7 &nstill&7 for a &b{standStillTime}&7, enter %1$s.
                                
                &6%1$s:
                Become &9invisible&7 and &cincrease&7 your rifle damage.
                                
                &8;;Taking damage, moving or un-scoping clears this effect.
                """.formatted(Named.REFRACTION), Material.FERMENTED_SPIDER_EYE);
    }
}
