package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

public class BloodfiendPassive extends PassiveTalent {

    @DisplayField public final int biteDuration = Tick.fromSecond(20);
    @DisplayField public final double healthDeduction = 10;
    @DisplayField public final int flightDuration = 40;
    @DisplayField public final int flightCooldown = Tick.fromSecond(12);
    @DisplayField public final double maxFlightHeight = 5;

    public BloodfiendPassive() {
        super("Vampire's Bite/Batransfer", Material.RED_DYE);

        setDescription("""
                &b&lVampire's Bite
                Your hits will inflict &csucculence&7 for &b{biteDuration}&7.
                                
                &cBitten &7players will suffer health reduction and can be affected by your abilities.
                                
                &b&lSpectral Form
                &6&lDOUBLE JUMP&7 to enter flight for a short duration to swiftly move around.
                You cannot transfer vertically.
                """);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
