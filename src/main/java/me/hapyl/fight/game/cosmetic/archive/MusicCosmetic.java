package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class MusicCosmetic extends Cosmetic {

    public MusicCosmetic() {
        super("Music", "Those are the tunes!", Type.KILL, Rarity.EPIC, Material.MUSIC_DISC_CHIRP);
    }

    @Override
    protected void onDisplay(Display display) {
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
