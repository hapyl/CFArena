package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class CoutureCosmetic extends Cosmetic {
    public CoutureCosmetic(Type type) {
        super("Couture", "Very stylish!", type);

        setExclusive(true);
        setRarity(Rarity.LEGENDARY);
        setIcon(Material.GOLD_INGOT);
    }

    public void displayKill(Location startLocation) {
        final Location location = startLocation.clone();

        new GameTask() {
            private double t = 0;
            private float pitch = 1.0f;

            @Override
            public void run() {
                pitch += Math.min(0.015, 2.0f);
                t += Math.PI / 16;

                PlayerLib.playSound(location, Sound.ENTITY_CHICKEN_EGG, pitch);
                PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, pitch);
                PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, pitch - 0.5f);

                double x = 0, y = 0, z = 0;
                for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 4) {
                    x = 0.3 * (4 * Math.PI - t) * Math.cos(t + phi);
                    y = 0.2 * t;
                    z = 0.3 * (4 * Math.PI - t) * Math.sin(t + phi);
                    location.add(x, y, z);
                    PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0.0d, 0.0d, 0.0d, 0.0f);
                    location.subtract(x, y, z);
                }

                if (t >= 4 * Math.PI) {
                    location.add(x, y, z);
                    PlayerLib.spawnParticle(location, Particle.CRIT, 40, 0.0d, 0.0d, 0.0d, 1f);
                    PlayerLib.playSound(location, Sound.ENTITY_PLAYER_BURP, 1.25f);
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 1);
    }

    public void displayDeath(Location startLocation) {
        final Location location = startLocation.clone();

        new GameTask() {
            private double phi = 0;

            @Override
            public void run() {
                phi += Math.PI / 6;
                double x, y, z;

                PlayerLib.playSound(location, Sound.ENTITY_SILVERFISH_AMBIENT, 1.75f);

                for (double t = 0; t < 2 * Math.PI; t += Math.PI / 16) {
                    for (double i = 0; i < 2; i++) {
                        x = 0.3 * (2 * Math.PI - t) * Math.cos(t + phi + i * Math.PI);
                        y = 0.5 * t;
                        z = 0.3 * (2 * Math.PI - t) * Math.sin(t + phi + i * Math.PI);
                        location.add(x, y, z);
                        PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0.0d, 0.0d, 0.0d, 0.0f);
                        location.subtract(x, y, z);
                    }
                }
                if (phi > 7 * Math.PI) {
                    PlayerLib.playSound(location, Sound.ENTITY_SILVERFISH_DEATH, 1.25f);
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 2);
    }

    @Override
    public void onDisplay(Display display) {
        if (getType() == Type.KILL) {
            displayKill(display.getLocation());
        }
        else {
            displayDeath(display.getLocation());
        }
    }
}
