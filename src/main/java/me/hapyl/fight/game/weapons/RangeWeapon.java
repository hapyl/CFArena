package me.hapyl.fight.game.weapons;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public abstract class RangeWeapon extends Weapon {

    private int cooldown;
    private double shift;
    private double maxDistance;

    private PackedParticle particleTick;
    private PackedParticle particleHit;

    private Sound sound;
    private float pitch;

    public RangeWeapon(Material material, String id) {
        super(material);
        this.cooldown = 0;
        this.shift = 0.5d;
        this.maxDistance = 40.0d;
        this.setId(id);
    }



    public RangeWeapon setMaxDistance(double d) {
        this.maxDistance = d;
        return this;
    }

    public RangeWeapon setShift(double d) {
        this.shift = d;
        return this;
    }

    public RangeWeapon setParticleHit(PackedParticle particleHit) {
        this.particleHit = particleHit;
        return this;
    }

    public RangeWeapon setParticleTick(PackedParticle particleTick) {
        this.particleTick = particleTick;
        return this;
    }

    public RangeWeapon setCooldown(int cd) {
        this.cooldown = cd;
        return this;
    }

    public RangeWeapon setCooldownSec(int cd) {
        return setCooldown(cd * 20);
    }

    // override for custom per-player cooldown
    public int getWeaponCooldown(Player player) {
        return cooldown;
    }

    public final int getWeaponCooldown() {
        return cooldown;
    }

    // override for custom per-player max distance
    public double getMaxDistance(Player player) {
        return maxDistance;
    }

    public final double getMaxDistance() {
        return maxDistance;
    }

    // override for custom per-player damage
    public double getDamage(Player player) {
        return getDamage();
    }

    @Nullable
    public EnumDamageCause getDamageCause(Player player) {
        return null;
    }

    /**
     * Called whenever entity was hit by the path.
     *
     * @param player - Shooter.
     * @param entity - Hit entity.
     */
    public void onHit(Player player, LivingEntity entity) {
    }

    /**
     * Called every move of the path.
     *
     * @param player   - Player.
     * @param location - Current path location.
     */
    public void onMove(Player player, Location location) {
    }

    /**
     * Called once upon player "pulling the trigger".
     *
     * @param player - Player.
     */
    public void onShoot(Player player) {
    }

    public boolean predicateBlock(Block block) {
        return !block.getType().isOccluding();
    }

    public boolean predicateEntity(LivingEntity entity) {
        return true;
    }

    public int getCooldown(Player player) {
        return player.getCooldown(getMaterial());
    }

    public void startCooldown(Player player) {
        if (this.cooldown > 0) {
            startCooldown(player, this.cooldown);
        }
    }

    public void startCooldown(Player player, int cd) {
        player.setCooldown(getMaterial(), cd);
    }

    public boolean hasCooldown(Player player) {
        return getCooldown(player) > 0;
    }

    public RangeWeapon setSound(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
        return this;
    }

    @Override
    public final void onRightClick(Player player, ItemStack item) {
        if (hasCooldown(player)) {
            return;
        }

        final double maxDistance = getMaxDistance(player);
        final int weaponCooldown = getWeaponCooldown(player);

        this.onShoot(player);

        Nulls.runIfNotNull(sound, s -> {
            PlayerLib.playSound(player.getLocation(), s, pitch);
        });

        final Location location = player.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();

        startCooldown(player, weaponCooldown);

        for (double i = 0; i < maxDistance; i += shift) {
            final double x = vector.getX() * i;
            final double y = vector.getY() * i;
            final double z = vector.getZ() * i;

            location.add(x, y, z);

            // check for block predicate
            if (!predicateBlock(location.getBlock())) {
                Nulls.runIfNotNull(particleHit, p -> {
                    p.display(location);
                });
                break;
            }

            for (final LivingEntity target : Collect.nearbyLivingEntities(location, 0.5d)) {
                if (target == player || !predicateEntity(target)) {
                    continue;
                }

                this.onHit(player, target);

                GamePlayer.damageEntity(target, getDamage(player), player, getDamageCause(player));

                Nulls.runIfNotNull(particleHit, p -> {
                    p.display(location);
                });
                return;
            }

            if (i > 1.0) {
                Nulls.runIfNotNull(particleTick, p -> {
                    p.display(location);
                });

                this.onMove(player, location);
            }

            location.subtract(x, y, z);
        }
    }

}
