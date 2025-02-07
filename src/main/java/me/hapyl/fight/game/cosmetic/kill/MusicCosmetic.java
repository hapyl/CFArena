package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class MusicCosmetic extends Cosmetic {

    public MusicCosmetic(@Nonnull Key key) {
        super(key, "Music", Type.KILL);

        setDescription("""
                Those are the tunes!
                """
        );

        setRarity(Rarity.EPIC);
        setIcon(Material.MUSIC_DISC_CHIRP);

        setExclusive(true);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        new TickingGameTask() {
            private float pitch = 1.5f;

            @Override
            public void run(int tick) {
                if (pitch > 2.0f) {
                    cancel();
                    return;
                }

                final Location location = display.getLocation();

                PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, pitch);
                PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, pitch);
                PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, pitch);

                PlayerLib.spawnParticle(location, Particle.NOTE, 1);

                pitch += 0.2f;
            }
        }.runTaskTimer(0, 2);
    }
}
