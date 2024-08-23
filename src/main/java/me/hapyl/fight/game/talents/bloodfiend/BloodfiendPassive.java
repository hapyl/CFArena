package me.hapyl.fight.game.talents.bloodfiend;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BloodfiendPassive extends PassiveTalent {

    @DisplayField public final int biteDuration = Tick.fromSecond(15);
    @DisplayField public final double healthDeduction = 10;
    @DisplayField public final int flightDuration = 40;
    @DisplayField public final int flightCooldown = Tick.fromSecond(12);
    @DisplayField public final double maxFlightHeight = 6;

    public BloodfiendPassive(@Nonnull DatabaseKey key) {
        super(key, "Vampire's Bite/Spectral Form");

        setDescription("""
                &b&l&nVampire's Bite
                Your hits will inflict &csucculence&7 for &b{biteDuration}&7.
                
                &cBitten &7players will suffer health reduction and can be affected by your talents.
                
                &b&l&nSpectral Form
                &6&lDOUBLE JUMP&7 to summon a swarm of bats and ride them, allowing to move swiftly for a short duration.
                
                You &ccannot&7 transfer vertically.
                You &acan&7 use talents, deal and take damage.
                """
        );

        setItem(Material.RED_DYE);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
