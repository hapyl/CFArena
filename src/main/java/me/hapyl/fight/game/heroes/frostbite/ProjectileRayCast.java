package me.hapyl.fight.game.heroes.frostbite;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.game.weapons.range.WeaponRayCast;
import me.hapyl.fight.util.Vector3;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ProjectileRayCast extends WeaponRayCast {

    private final double step;
    private final int iterations;

    public ProjectileRayCast(@Nonnull RangeWeapon weapon, @Nonnull GamePlayer player, double step, int iterations) {
        super(weapon, player);

        this.step = step;
        this.iterations = iterations;
    }

    @Override
    public void cast() {
        final Location location = player.getEyeLocation();

        this.onStart();

        new RaycastTask(location) {
            @Override
            public boolean step(@Nonnull Location location) {
                if (hitNearbyEntityAndCallOnHit(location)) {
                    return true;
                }

                onMove(location);
                spawnParticleTick(location);
                return false;
            }

            @Override
            public boolean predicate(@Nonnull Location location) {
                return canPassThrough(location.getBlock(), Vector3.of(location));
            }

            @Override
            public void onTaskStop() {
                onStop();
            }
        }.setStep(step).setMax(getMaxDistance()).setIterations(iterations).runTaskTimer(0, 1);
    }
}
