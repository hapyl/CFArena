package me.hapyl.fight.game.weapons.range;

import me.hapyl.fight.annotate.OverridingMethodsMustImplementEvents;
import me.hapyl.fight.event.PlayerHandler;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public class WeaponRayCast {

    protected final RangeWeapon weapon;
    protected GamePlayer player;

    private Vector vector;
    private boolean deflected;

    public WeaponRayCast(@Nonnull RangeWeapon weapon, @Nonnull GamePlayer player) {
        this.weapon = weapon;
        this.player = player;
    }

    @Event
    public void onStart() {
    }

    @Event
    public void onStop() {
    }

    @Event
    public void onMove(@Nonnull Location location) {
    }

    public boolean predicateBlock(@Nonnull Block block) {
        return !block.getType().isOccluding();
    }

    @OverridingMethodsMustImplementEvents()
    public void cast() {
        double maxDistance = getMaxDistance();
        final Location location = player.getEyeLocation();

        this.vector = player.getDirectionWithMovementError(weapon.movementError);

        boolean overrideDeflect = false;
        this.onStart();

        for (double i = 0; i < maxDistance; i += weapon.getShift()) {
            final double x = vector.getX() * i;
            final double y = vector.getY() * i;
            final double z = vector.getZ() * i;

            location.add(x, y, z);

            // Check for block predicate
            if (!predicateBlock(location.getBlock())) {
                this.spawnParticleHit(location);
                break;
            }

            // Hit detection
            if (hitNearbyEntityAndCallOnHit(location)) {
                this.onStop();
                return;
            }

            // Change distance if deflected
            if (deflected && !overrideDeflect) {
                maxDistance = maxDistance - i;
                i = 0.0d;
                overrideDeflect = true;
                continue;
            }

            // Only display particles after traveled at least one block to not block the vision to much
            if (i > 1.0) {
                spawnParticleTick(location);
                this.onMove(location);
            }

            location.subtract(x, y, z);
        }

        this.onStop();
    }

    public float getEntityDetectionRange() {
        return 1.0f;
    }

    public double getDamage(boolean isHeadShot) {
        return isHeadShot ? weapon.getDamage() * RangeWeapon.HEADSHOT_MULTIPLIER : weapon.getDamage();
    }

    public double getMaxDistance() {
        return weapon.getMaxDistance();
    }

    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.RANGE_ATTACK;
    }

    @Event
    @OverridingMethodsMustInvokeSuper
    public void onHit(@Nonnull LivingGameEntity entity, boolean isHeadShot) {
        entity.damage(getDamage(isHeadShot), player, getDamageCause());
    }

    @Event
    public boolean predicateEntity(@Nonnull LivingGameEntity entity) {
        return true;
    }

    @Nonnull
    public PackedParticle getParticleHit() {
        return weapon.getParticleHit();
    }

    @Nonnull
    public PackedParticle getParticleTick() {
        return weapon.getParticleTick();
    }

    protected boolean hitNearbyEntityAndCallOnHit(@Nonnull Location location) {
        final LivingGameEntity target = firstNearbyEntity(player, location, getEntityDetectionRange());

        if (target == null) {
            return false;
        }

        // Deflect
        if (!deflected && target instanceof GamePlayer playerTarget) {
            if (playerTarget.isDeflecting()) {
                this.player = playerTarget;
                this.vector = playerTarget.getDirectionWithMovementError(weapon.movementError);
                this.deflected = true;
                return false;
            }
        }

        final boolean isHeadShot = isHeadShot(location, target);

        target.modifyKnockback(PlayerHandler.RANGE_KNOCKBACK_RESISTANCE, then -> {
            onHit(then, isHeadShot);
        });

        spawnParticleHit(location);
        return true;
    }

    @Nullable
    protected LivingGameEntity firstNearbyEntity(GamePlayer player, Location location, double radius) {
        for (LivingGameEntity entity : Collect.nearbyEntities(location, radius)) {
            if (entity == null || player.isSelfOrTeammate(entity) || !predicateEntity(entity)) {
                continue;
            }

            return entity;
        }

        return null;
    }

    protected void spawnParticleHit(Location location) {
        getParticleHit().display(location);
    }

    protected void spawnParticleTick(Location location) {
        getParticleTick().display(location);
    }

    protected boolean isHeadShot(Location location, LivingGameEntity entity) {
        final double distanceToHead = location.distance(entity.getEyeLocation());
        return distanceToHead <= RangeWeapon.HEADSHOT_THRESHOLD;
    }
}
