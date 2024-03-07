package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

public class BloodfiendPassive extends PassiveTalent {

    @DisplayField public final int biteDuration = Tick.fromSecond(15);
    @DisplayField public final double healthDeduction = 10;
    @DisplayField public final int flightDuration = 40;
    @DisplayField public final int flightCooldown = Tick.fromSecond(12);
    @DisplayField public final double maxFlightHeight = 6;

    public BloodfiendPassive() {
        super("Vampire's Bite/Spectral Form", Material.RED_DYE);

        setDescription("""
                &b&lVampire's Bite
                Your hits will inflict &csucculence&7 for &b{biteDuration}&7.
                                
                &cBitten &7players will suffer health reduction and can be affected by your talents.
                                
                &b&lSpectral Form
                &6&lDOUBLE JUMP&7 to summon a swarm of bats and ride them, allowing to move swiftly for a short duration.
                                
                You &ccannot&7 transfer vertically.
                You &acan&7 use talents, deal and take damage.
                """);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
