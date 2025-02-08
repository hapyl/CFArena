package me.hapyl.fight.game.talents.bloodfiend;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BloodfiendPassive extends PassiveTalent {

    @DisplayField public final int biteDuration = Tick.fromSecond(15);
    @DisplayField public final double healthDeduction = 10;

    public BloodfiendPassive(@Nonnull Key key) {
        super(key, "Vampire's Bite");

        setDescription("""
                &b&l&nVampire's Bite
                Your hits will inflict &csucculence&7 for &b{biteDuration}&7.
                
                &cBitten &7players will suffer health reduction and can be affected by your talents.
                """
        );

        setItem(Material.RED_DYE);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
