package me.hapyl.fight.game.heroes.moonwalker;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.moonwalker.MoonPillarZone;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public class MoonwalkerData extends PlayerData implements Ticking {

    public static final EntityCooldown COOLDOWN = EntityCooldown.of("ray_of_death", 100L);

    public static final int MAX_ZONES = 3;
    public static final int ICD = 4;

    private final LinkedList<MoonZone> moonZones;

    protected int heldTimes;
    protected int lastHeldAt;

    protected double energy;

    public MoonwalkerData(GamePlayer player) {
        super(player);

        this.moonZones = Lists.newLinkedList();
    }

    public void incrementMoonEnergy(double amount) {
        this.energy = Math.min(this.energy + amount, HeroRegistry.MOONWALKER.getPassiveTalent().maxEnergy);

        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 1.25f);
    }

    @Override
    public void tick() {
        final MoonwalkerWeapon weapon = weapon();

        final int aliveTicks = player.ticker.aliveTicks.getTick();
        final int ticksSinceLastHeld = aliveTicks - this.lastHeldAt;
        final boolean isCharging = this.heldTimes <= weapon.chargingTime;

        // Player stopped charging, execute small hit
        if (this.lastHeldAt != 0) {
            if (ticksSinceLastHeld >= weapon.maxInactiveTime) {
                onStopHeld(isCharging);
            }
            else if (isCharging) {
                onHeldCharge((double) this.heldTimes / weapon.chargingTime);
            }
            else {
                onHeldAttack();
            }
        }

        // Tick moon zones
        moonZones.removeIf(MoonZone::removeIfShould);
        moonZones.forEach(MoonZone::tick);
    }

    @EventLike
    public void onHeldAttack() {
        final MoonwalkerWeapon weapon = weapon();

        // Check fuel
        if (energy < weapon.energyToActivate) {
            onStopHeld(false);

            player.sendSubtitle("&6&lᴏᴜᴛ ᴏꜰ ᴇɴᴇʀɢʏ", 0, 15, 5);
            player.playWorldSound(Sound.ENTITY_VEX_HURT, 0.0f);
            return;
        }

        // Decrement fuel
        energy -= weapon.energyDrainPerTick;

        player.addPotionEffect(PotionEffectType.SLOWNESS, 2, 5);

        // Attack
        final Location location = player.getMidpointLocation();
        final Vector vector = location.getDirection();

        for (double d = 0.0d; d < weapon.maxDistance; d += 0.5d) {
            location.add(vector);

            // Collision detection
            if (!location.getBlock().isPassable()) {
                player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 5, 0.3d, 0.3d, 0.3d, 0.1f);
                break;
            }

            // Handle damage this way because we want to make this ability AoE
            Collect.nearbyEntities(location, weapon.hitBoxSize, player::isNotSelfOrTeammate)
                   .forEach(entity -> {
                       if (entity.isNotOnCooldownAndStart(COOLDOWN)) {
                           entity.damage(weapon().damage, player, DamageCause.RAY_OF_DEATH);

                           // Fx
                           entity.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.75f);
                           entity.spawnWorldParticle(Particle.WITCH, 10, 0.2d, 0.6d, 0.2d, 0.05f);
                       }
                   });

            final int aliveTicks = player.aliveTicks();

            final double x = Math.sin(aliveTicks) * 0.3d;
            final double y = Math.sin(aliveTicks * 5) * 0.1d;
            final double z = Math.cos(aliveTicks) * 0.3d;

            LocationHelper.offset(
                    location, x, y, z, () -> {
                        player.spawnWorldParticle(location, Particle.WITCH, 1);
                    }
            );
        }

        // Fx
        player.playWorldSound(Sound.BLOCK_SCULK_CHARGE, 0.0f);
    }

    @EventLike
    public void onHeldCharge(double percentCharged) {
        // Fx
        player.playWorldSound(Sound.ENTITY_PLAYER_BURP, (float) (0.75f + (0.5f * percentCharged)));
        player.addPotionEffect(PotionEffectType.SLOWNESS, 5, 2);

        // Progress
        final int progress = (int) (20 * percentCharged);

        player.sendSubtitle(
                ("&e&l⚡".repeat(progress) + "&8&l⚡".repeat(20 - progress)),
                0, 5, 5
        );
    }

    @EventLike
    public void onStopHeld(boolean isCharging) {
        this.lastHeldAt = 0;
        this.heldTimes = 0;

        // If was charging, execute a small explosion
        if (isCharging) {
            Debug.info("Small hit");
        }

        player.sendSubtitle("&4&lᴄᴀɴᴄᴇʟʟᴇᴅ", 0, 5, 5);
        player.playWorldSound(Sound.ENTITY_PLAYER_BURP, 0.0f);

        // Start cooldown
        weapon().ability().startCooldown(player);
    }

    @Override
    public void remove() {
        moonZones.forEach(MoonZone::remove);
        moonZones.clear();
    }

    public void addZone(MoonPillarZone zone) {
        if (moonZones.size() >= MAX_ZONES) {
            final MoonZone last = moonZones.pollFirst();

            if (last != null) {
                last.remove();
            }
        }

        moonZones.add(zone);
    }

    @Nullable
    public MoonZone getZone(@Nonnull Location location) {
        for (MoonZone zone : moonZones) {
            if (zone.centre.distance(location) <= zone.size && zone.energy > 1) {
                return zone;
            }
        }

        return null;
    }

    public void incrementWeaponCharge() {
        if (this.lastHeldAt != 0 && player.ticker.aliveTicks.getTick() - this.lastHeldAt < ICD) {
            return;
        }

        this.heldTimes++;
        this.lastHeldAt = player.ticker.aliveTicks.getTick();
    }

    private MoonwalkerWeapon weapon() {
        return HeroRegistry.MOONWALKER.getWeapon();
    }
}
