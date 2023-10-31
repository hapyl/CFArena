package me.hapyl.fight.game.entity;

import me.hapyl.spigotutils.module.math.geometry.Draw;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class FerocityFx extends Draw {

    public FerocityFx() {
        super(null);
    }

    @Override
    public void draw(Location location) {
        final World world = location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(
                Particle.DUST_COLOR_TRANSITION,
                location,
                1,
                0,
                0,
                0,
                new Particle.DustTransition(Color.fromRGB(77, 2, 8), Color.fromRGB(181, 43, 54), 1)
        );
    }
}
