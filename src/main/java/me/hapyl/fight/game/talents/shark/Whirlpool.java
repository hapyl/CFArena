package me.hapyl.fight.game.talents.shark;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.fx.RiptideFx;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Whirlpool extends Talent {

    @DisplayField private final double radius = 4.0d;
    @DisplayField private final double pullStrength = 0.5d;
    @DisplayField private final double damage = 6.0d;

    @DisplayField private final int period = 5;

    public Whirlpool(@Nonnull Key key) {
        super(key, "Sharknado");

        setDescription("""
                Create a Sharknado in front of you that constantly pulls enemies towards the center.
                
                Enemies at the center of the Sharknado are lifter up and take continuous damage.
                """
        );

        setMaterial(Material.HEART_OF_THE_SEA);

        setDurationSec(8);
        setCooldownSec(16);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        final Vector vector = location.getDirection();
        vector.setY(0.0d).multiply(4.0);

        final RiptideFx riptide = new RiptideFx(location.add(vector));

        new TickingGameTask() {

            private double d;

            @Override
            public void onTaskStop() {
                riptide.remove();
            }

            @Override
            public void run(int tick) {
                if (tick >= getDuration()) {
                    cancel();
                    return;
                }

                // Pull enemies towards the center
                if (modulo(period)) {
                    Collect.nearbyEntities(location, radius).forEach(entity -> {
                        if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                            //return;
                        }

                        final Location entityLocation = entity.getLocation();
                        final Vector vector = location.clone().subtract(entityLocation).toVector().normalize().multiply(pullStrength);

                        // Center
                        if (entityLocation.distanceSquared(location) <= 1.0d) {
                            vector.setY(BukkitUtils.GRAVITY * 2);

                            entity.damage(damage, player);
                        }

                        entity.setVelocity(vector);
                    });
                }

                // Fx
                final double x = Math.sin(d) * radius;
                final double y = Math.atan(Math.toRadians(tick * 8)) * 1d;
                final double z = Math.cos(d) * radius;

                LocationHelper.offset(location, x, y, z, () -> {
                    player.spawnWorldParticle(location, Particle.FLAME, 1);
                });

                LocationHelper.offset(location, z, y, x, () -> {
                    player.spawnWorldParticle(location, Particle.FLAME, 1);
                });

                d += Math.PI / 32;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
