package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Material;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class MusicContrailCosmetic extends ParticleContrailCosmetic {

    public MusicContrailCosmetic(@Nonnull Key key) {
        super(key, Particle.NOTE, "Music", "Leave a note behind you.",  Rarity.COMMON);

        setIcon(Material.MUSIC_DISC_CAT);
    }

    @Override
    public void onMove(@Nonnull Display display, int tick) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // This is really the weirdest way to handle particle color.
        display.particle(
                getParticle(),
                0,
                random.nextDouble(0.0d, 1.0d),
                random.nextDouble(0.0d, 1.0d),
                random.nextDouble(0.0d, 1.0d),
                1.0f
        );
    }
}
