package me.hapyl.fight.game.weapons;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RangeWeapon extends Weapon implements GameElement, PlayerElement, UIComponent {

    public static final double HEADSHOT_THRESHOLD = 0.75d;
    private final PlayerMap<Integer> playerAmmo;
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

        this.playerAmmo = PlayerMap.newMap();
        this.maxAmmo = 8;
        this.reloadTime = 100;

        setAbility(AbilityType.RIGHT_CLICK, new AbilityShoot());
        setAbility(AbilityType.LEFT_CLICK, new AbilityReload());
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        playerAmmo.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        playerAmmo.clear();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
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

    public RangeWeapon setReloadTimeSec(int reloadTimeSec) {
        return setReloadTime(reloadTimeSec * 20);
    }

    public RangeWeapon setCooldownSec(int cd) {
        return setCooldown(cd * 20);
    }

    // override for custom per-player cooldown
    public int getWeaponCooldown(@Nonnull GamePlayer player) {
        return cooldown;
    }

    public final int getWeaponCooldown() {
        return cooldown;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public RangeWeapon setReloadTime(int reloadTime) {
        this.reloadTime = reloadTime;
        return this;
    }

    // override for custom per-player max distance
    public double getMaxDistance(@Nonnull GamePlayer player) {
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
    public double getDamage(@Nonnull GamePlayer player, boolean headshot) {
        return getDamage();
    }

    @Nullable
    public EnumDamageCause getDamageCause(@Nonnull GamePlayer player) {
        return null;
    }

    /**
     * Called whenever the path hit entity.
     *
     * @param player   - Shooter.
     * @param entity   - Hit entity.
     * @param headshot - Whenever the hit was a headshot or not.
     */
    public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, boolean headshot) {
    }

    /**
     * Called every move of the path.
     *
     * @param player   - Player.
     * @param location - Current path location.
     */
    public void onMove(@Nonnull GamePlayer player, Location location) {
    }

    /**
     * Called once upon player "pulling the trigger."
     *
     * @param player - Player.
     */
    public void onShoot(@Nonnull GamePlayer player) {
    }

    public boolean predicateBlock(Block block) {
        return !block.getType().isOccluding();
    }

    public boolean predicateEntity(LivingGameEntity entity) {
        return true;
    }

    public int getCooldown(GamePlayer player) {
        return player.getCooldown(getMaterial());
    }

    public void startCooldown(GamePlayer player) {
        if (this.cooldown > 0) {
            startCooldown(player, this.cooldown);
        }
    }

    public void startCooldown(GamePlayer player, int cd) {
        player.setCooldown(getMaterial(), cd);
    }

    public boolean hasCooldown(GamePlayer player) {
        return getCooldown(player) > 0;
    }

    public RangeWeapon setSound(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
        return this;
    }

    public void forceReload(@Nonnull GamePlayer player) {
        playerAmmo.put(player, getMaxAmmo());
    }

    public void reload(@Nonnull GamePlayer player) {
        final ItemStack item = player.getItem(HotbarSlots.WEAPON);

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
                    player.playWorldSound(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.75f);
                    player.playWorldSound(Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.25f);
                }

                // This just adds a reload durability animation because it looks kinda cool
                final float progress = (float) tick / reloadTime;

                if (item == null || item.getType() != getMaterial()) {
                    return;
                }

                final short maxDurability = getMaterial().getMaxDurability();

                // This can actually break the item :/
                //modifyMeta(item, meta -> {
                //    if (meta instanceof Damageable damageable) {
                //        damageable.setDamage(maxDurability - (int) (progress * maxDurability));
                //        damageable.setUnbreakable(false);
                //    }
                //});
            }
        }.runTaskTimer(0, 1);

        startCooldown(player, reloadTime);
    }

    public int getPlayerAmmo(GamePlayer player) {
        return playerAmmo.computeIfAbsent(player, fn -> maxAmmo);
    }

    private int subtractAmmo(GamePlayer player) {
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

    public class AbilityReload extends Ability {

        public AbilityReload() {
            super("Reload!", "Reload your weapon manually.");
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (player.hasCooldown(getType()) || getPlayerAmmo(player) >= maxAmmo) {
                return null;
            }

            reload(player);
            return Response.AWAIT;
        }
    }

    public class AbilityShoot extends Ability {
        public AbilityShoot() {
            super("Shoot!", "Shoot your weapon.");
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (player.hasCooldown(getType())) {
                return null;
            }

            final double maxDistance = getMaxDistance(player);
            final int weaponCooldown = getWeaponCooldown(player);

            onShoot(player);

            Nulls.runIfNotNull(sound, sound -> {
                player.playWorldSound(sound, pitch);
            });

            final Location location = player.getLocation().add(0, 1.5, 0);
            final Vector vector = location.getDirection().normalize();

            // Cooldown and Ammunition
            final int ammo = subtractAmmo(player);

            if (ammo <= 0) {
                reload(player);
            }
            else {
                RangeWeapon.this.startCooldown(player, weaponCooldown);
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
                    if (target == null || target.equals(player) || !predicateEntity(target)) {
                        continue;
                    }

                    final double distanceToHead = location.distance(target.getEyeLocation());
                    final boolean isHeadShot = distanceToHead <= HEADSHOT_THRESHOLD;

                    onHit(player, target, isHeadShot);

                    target.modifyKnockback(0.5d, d -> {
                        d.damage(getDamage(player, false), player, getDamageCause(player));
                    });

                    Nulls.runIfNotNull(particleHit, p -> {
                        p.display(location);
                    });

                    return Response.OK;
                }

                if (i > 1.0) {
                    Nulls.runIfNotNull(particleTick, p -> {
                        p.display(location);
                    });

                    onMove(player, location);
                }

                location.subtract(x, y, z);
            }

            return Response.OK;
        }
    }

}
