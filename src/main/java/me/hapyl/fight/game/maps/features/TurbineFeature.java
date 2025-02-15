package me.hapyl.fight.game.maps.features;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.Direction;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.maps.LevelFeature;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.BoundingBoxCollector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class TurbineFeature extends LevelFeature {

    private final List<Turbine> turbines;

    @Nonnull private final World world = Bukkit.getWorlds().getFirst();

    public TurbineFeature() {
        super(
                "Turbines",
                "It's the ultimate suckfest! Once you're in, there's no way out. Survive or get swallowed by its merciless power."
        );

        turbines = Lists.newArrayList();
    }

    public TurbineFeature addTurbine(Turbine turbine) {
        turbines.add(turbine);
        return this;
    }

    @Override
    public void tick(int tick) {
        turbines.forEach(turbine -> {
            final BoundingBoxCollector boundingBox = turbine.getBoundingBox();
            final BoundingBoxCollector killTrigger = turbine.getKillTrigger();

            // Suck em all
            boundingBox.collect(world).forEach(entity -> {
                entity.setVelocity(entity.getVelocity().add(turbine.getVector()).setY(0.0d));
            });

            // Kill em all
            killTrigger.collect(world).forEach(entity -> {
                entity.damage(entity.getHealth() + 100, DamageCause.SHREDS_AND_PIECES);

                // Fx
                Registries.getCosmetics().BLOOD.onDisplay0(new Display(null, entity.getLocation()));

                // Trigger achievement
                if (entity instanceof Player player) {
                    Registries.getAchievements().SHREDDING_TIME.complete(player);
                }
            });

            // Display Fx
            final Location fxLocation = turbine.getFxLocation();

            if (tick % 20 != 0) {
                return;
            }

            final Direction direction = turbine.getDirection();

            PlayerLib.spawnParticle(
                    fxLocation,
                    Particle.POOF,
                    10,
                    direction.getValue(0, 3.0d, 0.5d),
                    direction.getValue(1, 3.0d, 0.5d),
                    direction.getValue(2, 3.0d, 0.5d),
                    0.08f
            );

            final double radius = turbine.getRadius();

            for (double d = 0.0d; d < Math.PI * 2; d += Math.PI / 32) {
                final double x = !direction.isAxisX() ? Math.sin(d) * radius : 0.0d;
                final double y = Math.cos(d) * radius;
                final double z = !direction.isAxisZ() ? Math.sin(d) * radius : 0.0d;

                fxLocation.add(x, y, z);

                PlayerLib.spawnParticle(fxLocation, Particle.CRIT, 1);

                fxLocation.subtract(x, y, z);
            }

        });
    }
}
