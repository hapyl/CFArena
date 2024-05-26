package me.hapyl.fight.util.particle;

import javax.annotation.Nonnull;
import java.awt.*;

// TODO (hapyl): 021, Mar 21: Add this to eterna, it's particle builder sucks balls
public interface Particles {

    @Nonnull
    static ParticleDrawer mobSpell(int red, int green, int blue) {
        return new ParticleSpellMob(red, green, blue);
    }

    @Nonnull
    static ParticleDrawer mobSpell(@Nonnull Color color) {
        return mobSpell(color.getRed(), color.getGreen(), color.getBlue());
    }

}
