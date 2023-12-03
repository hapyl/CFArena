package me.hapyl.fight.game.weapons;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public abstract class RangeWeapon extends Weapon implements GameElement, PlayerElement, UIComponent {

    private final Map<Player, Integer> playerAmmo;

    private int reloadTime;
    private int maxAmmo;
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

        this.playerAmmo = Maps.newHashMap();
        this.maxAmmo = 8;
        this.reloadTime = 100;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        playerAmmo.clear();
    }

    @Override
    public void onDeath(Player player) {
        playerAmmo.clear();
    }

    @Nonnull
    @Override
    public String getString(Player player) {
        final int ammo = getPlayerAmmo(player);
        return "&3⁍ &b&l" + (ammo <= 0 ? "RELOADING" : ammo + "&b/&b&l" + maxAmmo);
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public RangeWeapon setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
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
        this.reloadTime = cd * 3;
        return this;
    }

    public RangeWeapon setReloadTime(int reloadTime) {
        this.reloadTime = reloadTime;
        return this;
    }

    public RangeWeapon setReloadTimeSec(int reloadTimeSec) {
        return setReloadTime(reloadTimeSec * 20);
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

    public int getReloadTime() {
        return reloadTime;
    }

    // override for custom per-player max distance
    public double getMaxDistance(Player player) {
        return maxDistance;
    }

    public final double getMaxDistance() {
        return maxDistance;
    }

    public RangeWeapon setMaxDistance(double d) {
        this.maxDistance = d;
        return this;
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
     * Called once upon player "pulling the trigger."
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
        GamePlayer.setCooldown(player, getMaterial(), cd);
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
    public final void onLeftClick(Player player, ItemStack item) {
        if (hasCooldown(player) || getPlayerAmmo(player) >= maxAmmo) {
            return;
        }

        reload(player);
    }

    public void reload(Player player) {
        final ItemStack item = player.getInventory().getItem(0);

        // force reload
        playerAmmo.put(player, 0);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= reloadTime) {
                    modifyMeta(item, meta -> meta.setUnbreakable(true));
                    playerAmmo.put(player, maxAmmo);
                    cancel();
                    return;
                }

                // Fx
                if (tick % 40 == 0) {
                    PlayerLib.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.75f);
                    PlayerLib.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.25f);
                }

                // This just adds a reload durability animation because it looks kinda cool
                final float progress = (float) tick / reloadTime;

                if (item == null || item.getType() != getMaterial()) {
                    return;
                }

                final short maxDurability = getMaterial().getMaxDurability();

                modifyMeta(item, meta -> {
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(maxDurability - (int) (progress * maxDurability));
                        damageable.setUnbreakable(false);
                    }
                });
            }
        }.runTaskTimer(0, 1);

        startCooldown(player, reloadTime);
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

        // Cooldown and Ammunition
        final int ammo = subtractAmmo(player);

        if (ammo <= 0) {
            reload(player);
        }
        else {
            startCooldown(player, weaponCooldown);
        }

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

            for (final LivingGameEntity target : Collect.nearbyEntities(location, 1.0d)) {
                final LivingEntity targetEntity = target.getEntity();
                if (target.is(player) || !predicateEntity(targetEntity)) {
                    continue;
                }

                this.onHit(player, targetEntity);

                target.modifyKnockback(0.5d, d -> {
                    d.damage(getDamage(player), CF.getPlayer(player), getDamageCause(player));
                });

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

    public int getPlayerAmmo(Player player) {
        return playerAmmo.computeIfAbsent(player, fn -> maxAmmo);
    }

    private int subtractAmmo(Player player) {
        return playerAmmo.compute(player, (p, i) -> i == null ? maxAmmo - 1 : i - 1);
    }

    private void modifyMeta(ItemStack item, Consumer<ItemMeta> consumer) {
        if (item == null) {
            return;
        }

        final ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        consumer.accept(meta);
        item.setItemMeta(meta);
    }

}
