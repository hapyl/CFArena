package me.hapyl.fight.game.weapons.range;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeaponRaycast {

    protected RangeWeapon weapon;

    public WeaponRaycast(RangeWeapon weapon) {
        this.weapon = weapon;
    }

    @Nonnull
    public WeaponRaycastInstance newInstance(@Nonnull GamePlayer player) {
        return new WeaponRaycastInstance(player, weapon);
    }

    // FIXME (hapyl): 010, Feb 10: This dog shit system sucks balls
    public void cast(@Nonnull GamePlayer player) {
        final WeaponRaycastInstance instance = newInstance(player);
        final double maxDistance = weapon.getMaxDistance(player);

        final Location location = player.getEyeLocation();
        final Vector vector = player.getDirectionWithMovementError(weapon.movementError);

        instance.onStart();

        for (double i = 0; i < maxDistance; i += weapon.getShift()) {
            final double x = vector.getX() * i;
            final double y = vector.getY() * i;
            final double z = vector.getZ() * i;

            location.add(x, y, z);

            // Check for block predicate
            if (!weapon.predicateBlock(player, location.getBlock())) {
                spawnParticleHit(location);
                break;
            }

            // Hit detection
            if (hitNearbyEntityAndCallOnHit(player, instance, location)) {
                instance.onStop();
                return;
            }

            // Only display particles after traveled at least one block to not block the vision to much
            if (i > 1.0) {
                spawnParticleTick(location);
                instance.onMove(location);
            }

            location.subtract(x, y, z);
        }

        instance.onStop();
    }

    protected boolean hitNearbyEntityAndCallOnHit(GamePlayer player, WeaponRaycastInstance instance, Location location) {
        final LivingGameEntity target = firstNearbyEntity(player, location, 1.0f);

        if (target == null) {
            return false;
        }

        final boolean isHeadShot = isHeadShot(location, target);

        target.modifyKnockback(weapon.knockback, then -> {
            instance.onHit(then, isHeadShot);
        });

        spawnParticleHit(location);
        return true;
    }

    @Nullable
    protected LivingGameEntity firstNearbyEntity(GamePlayer player, Location location, double radius) {
        for (LivingGameEntity entity : Collect.nearbyEntities(location, radius)) {
            if (entity == null || player.isSelfOrTeammate(entity) || !weapon.predicateEntity(entity)) {
                continue;
            }

            return entity;
        }

        return null;
    }

    protected void spawnParticleHit(Location location) {
        final PackedParticle particle = weapon.getParticleHit();
        if (particle != null) {
            particle.display(location);
        }
    }

    protected void spawnParticleTick(Location location) {
        final PackedParticle particle = weapon.getParticleTick();
        if (particle != null) {
            particle.display(location);
        }
    }

    protected boolean isHeadShot(Location location, LivingGameEntity entity) {
        final double distanceToHead = location.distance(entity.getEyeLocation());
        return distanceToHead <= RangeWeapon.HEADSHOT_THRESHOLD;
    }
}
