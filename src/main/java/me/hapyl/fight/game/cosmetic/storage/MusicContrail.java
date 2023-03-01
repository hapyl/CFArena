package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.contrail.ParticleContrailCosmetic;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Particle;

public class MusicContrail extends ParticleContrailCosmetic {

    public MusicContrail() {
        super(Particle.NOTE, "Music", "Leave a note behind you.", 500, Rarity.COMMON);
    }

    @Override
    public void onMove(Display display) {
        // This is really the weirdest way to handle particle color.
        display.particle(
                getParticle(),
                0,
                ThreadRandom.nextDouble(0.0d, 1.0d),
                ThreadRandom.nextDouble(0.0d, 1.0d),
                ThreadRandom.nextDouble(0.0d, 1.0d),
                1.0f
        );
    }
}
