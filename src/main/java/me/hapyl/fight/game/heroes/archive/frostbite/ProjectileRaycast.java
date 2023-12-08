package me.hapyl.fight.game.heroes.archive.frostbite;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.game.weapons.range.WeaponRaycast;
import me.hapyl.fight.game.weapons.range.WeaponRaycastInstance;
import me.hapyl.fight.game.weapons.range.WeaponRaycastable;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ProjectileRaycast extends WeaponRaycast {

    private final double step;
    private final int iterations;

    public ProjectileRaycast(WeaponRaycastable raycastable, double step, int iterations) {
        super(raycastable);

        this.step = step;
        this.iterations = iterations;
    }

    @Nonnull
    @Override
    public WeaponRaycastInstance newInstance(@Nonnull GamePlayer player) {
        return new WeaponRaycastInstance(player, raycastable);
    }

    @Override
    public void cast(@Nonnull GamePlayer player) {
        final WeaponRaycastInstance instance = newInstance(player);
        final Location location = player.getEyeLocation();

        instance.onStart();

        new RaycastTask(location) {
            @Override
            public boolean step(@Nonnull Location location) {
                if (hitNearbyEntityAndCallOnHit(player, instance, location)) {
                    return true;
                }

                instance.onMove(location);
                spawnParticleTick(location);
                return false;
            }

            @Override
            public boolean predicate(@Nonnull Location location) {
                return raycastable.predicateBlock(location.getBlock());
            }

            @Override
            public void onTaskStop() {
                instance.onStop();
            }
        }.setStep(step).setMax(raycastable.getMaxDistance(player)).setIterations(iterations).runTaskTimer(0, 1);
    }
}
