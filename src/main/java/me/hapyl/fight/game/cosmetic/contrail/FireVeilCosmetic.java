package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FireVeilCosmetic extends ContrailCosmetic {

    public FireVeilCosmetic(@Nonnull Key key) {
        super(
                key, "Fire Veil", """
                        A veil or fire surrounds your body.
                        
                        &8&oWarm...
                        """, Rarity.LEGENDARY, PARTICLE
        );

        setIcon(Material.BLAZE_POWDER);
    }

    @Override
    public void onStandingStill(@Nonnull Display display, int tick) {
        final double theta = tick * Math.PI / 16;

        final double x = Math.sin(theta) * 0.6d;
        final double y = Math.sin(Math.toRadians(tick * 5)) * 0.1d - 0.7d;
        final double z = Math.cos(theta) * 0.6d;

        final Location playerLocation = display.getPlayerLocationOrZero();

        final Consumer<Location> fx = location -> {
            final Vector vector = playerLocation.toVector().subtract(location.toVector()).normalize().multiply(-1);

            display.particle(Particle.FLAME, 0, vector.getX() * 0.075d, 0.1f, vector.getZ() * 0.075d, 1f);
            display.particle(Particle.SMOKE, 2, 0.025f);
        };

        display.offset(x, y, z, fx);
        display.offset(x * -1, y, z * -1, fx);
    }

    @Override
    public void onMove(@Nonnull Display display, int tick) {
        final Location playerLocation = display.getPlayerLocationOrZero();

        final double r = Math.toRadians(tick * 10);
        final double x = Math.sin(r) * 0.25d;
        final double y = Math.sin(r) * 0.075d - 0.5d;
        final double z = Math.cos(r) * 0.25d;

        final Consumer<Location> fx = location -> {
            final Vector vector = playerLocation.toVector().subtract(location.toVector()).normalize().multiply(-1);

            display.particle(Particle.FLAME, 0, vector.getX() * 0.1d, 0.02f, vector.getZ() * 0.01d, 1f);
            display.particle(Particle.SMOKE, 1, 0.025f);
        };

        display.offset(x, y, z, fx);
        display.offset(x * -1, y, z * -1, fx);
    }
}
