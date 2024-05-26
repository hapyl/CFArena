package me.hapyl.fight.game.weapons.range;

import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.collection.RangeTreeMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class RangeWeapon extends Weapon implements GameElement, PlayerElement, UIComponent {

    public static final double HEADSHOT_THRESHOLD = 0.75d;
    public static final double HEADSHOT_MULTIPLIER = 1.5d;

    private final PlayerMap<Integer> playerAmmo;

    protected double shift;
    protected double movementError;

    private int reloadTime;
    private int maxAmmo;
    private int cooldown;
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
        this.movementError = 0.3d;

        this.particleHit = PackedParticle.EMPTY;
        this.particleTick = PackedParticle.EMPTY;

        setAbility(AbilityType.RIGHT_CLICK, new AbilityShoot());
    }

    public double getMovementError() {
        return movementError;
    }

    public RangeWeapon setMovementError(double movementError) {
        this.movementError = movementError;
        return this;
    }

    @Override
    public final void onStart() {
    }

    @Override
    public final void onStop() {
        playerAmmo.clear();
    }

    @Override
    public final void onDeath(@Nonnull GamePlayer player) {
        playerAmmo.remove(player);
    }

    @Override
    public void appendLore(@Nonnull ItemBuilder builder) {
        builder.addLore();
        builder.addLore("&e&lᴀᴛᴛʀɪʙᴜᴛᴇs:");

        addDynamicLore(builder, " ғɪʀᴇ ʀᴀᴛᴇ: &f&l%s", cooldown, t -> Tick.round(t.intValue()) + "s");
        addDynamicLore(builder, " ᴍᴀx ᴅɪsᴛᴀɴᴄᴇ: &f&l%s", maxDistance, Object::toString);
        addDynamicLore(builder, " ᴅᴀᴍᴀɢᴇ: &f&l%s", damage, Object::toString);

        builder.addLore(" ᴍᴀx ᴀᴍᴍᴏ: &f&l%s".formatted(maxAmmo));
        builder.addLore(" ʀᴇʟᴏᴀᴅ ᴛɪᴍᴇ: &f&l%ss".formatted(Tick.round(reloadTime)));
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

    public RangeWeapon setCooldown(int cd) {
        this.cooldown = cd;
        this.reloadTime = cd * 3;
        return this;
    }

    @Nonnull
    public PackedParticle getParticleTick() {
        return particleTick;
    }

    public RangeWeapon setParticleTick(PackedParticle particleTick) {
        this.particleTick = particleTick;
        return this;
    }

    @Nonnull
    public PackedParticle getParticleHit() {
        return particleHit;
    }

    public RangeWeapon setParticleHit(PackedParticle particleHit) {
        this.particleHit = particleHit;
        return this;
    }

    public RangeWeapon setReloadTimeSec(int reloadTimeSec) {
        return setReloadTime(reloadTimeSec * 20);
    }

    public RangeWeapon setCooldownSec(int cd) {
        return setCooldown(cd * 20);
    }

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

    @Nonnull
    public WeaponRayCast newRayCastInstance(@Nonnull GamePlayer player) {
        return new WeaponRayCast(this, player);
    }

    public double getShift() {
        return shift;
    }

    public RangeWeapon setShift(double d) {
        this.shift = d;
        return this;
    }

    public final double getMaxDistance() {
        return maxDistance;
    }

    public RangeWeapon setMaxDistance(double d) {
        this.maxDistance = d;
        return this;
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
        final int reloadTimeScaled = player.scaleCooldown(reloadTime);

        // force reload
        playerAmmo.put(player, 0);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= reloadTimeScaled) {
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

    public int getPlayerAmmo(@Nonnull GamePlayer player) {
        return playerAmmo.computeIfAbsent(player, fn -> maxAmmo);
    }

    public int getWeaponCooldownScale(GamePlayer player) {
        final int weaponCooldown = getWeaponCooldown(player);

        return player.getAttributes().calculateRangeAttackSpeed(weaponCooldown);
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

    private void playShootSound(Location location) {
        if (sound != null) {
            PlayerLib.playSound(location, sound, pitch);
        }
    }

    public class AbilityShoot extends Ability {
        public AbilityShoot() {
            super("Shoot!", "Shoot your weapon.");
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (player.hasCooldown(getMaterial())) {
                return null;
            }

            playShootSound(player.getLocation());
            final WeaponRayCast instance = newRayCastInstance(player);

            // Cooldown and Ammunition
            final int ammo = subtractAmmo(player);

            instance.cast();

            if (ammo <= 0) {
                reload(player);
            }
            else {
                final int weaponCooldownScale = getWeaponCooldownScale(player);

                player.setCooldownIgnoreModifier(getMaterial(), weaponCooldownScale);
            }

            return Response.OK;
        }
    }

}
