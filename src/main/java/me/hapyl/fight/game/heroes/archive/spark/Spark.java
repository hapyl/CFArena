package me.hapyl.fight.game.heroes.archive.spark;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.RangeWeapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Spark extends Hero implements PlayerElement {

    private final PlayerMap<RunInBackData> markerLocation = PlayerMap.newMap();

    public Spark() {
        super("Spark");

        setArchetype(Archetype.RANGE);

        setDescription("Strikes as fire with his fire abilities.");
        setItem("ade095332720215ca9b85e7eacd1d092b1697fad34d696add94d3b70976702c");

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(Color.ORANGE);
        equipment.setLeggings(Color.RED);
        equipment.setBoots(Color.ORANGE);

        setWeapon(new RangeWeapon(Material.STICK, "fire_weapon") {
            @Override
            public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, boolean headshot) {
                entity.setFireTicks(10);
            }

            @Nonnull
            @Override
            public EnumDamageCause getDamageCause(@Nonnull GamePlayer player) {
                return EnumDamageCause.FIRE_SPRAY;
            }
        }.setCooldown(30)
                .setSound(Sound.ENTITY_BLAZE_SHOOT, 1.75f)
                .setParticleHit(new PackedParticle(Particle.LAVA).setAmount(3).setSpeed(0.2f))
                .setParticleTick(new PackedParticle(Particle.FLAME).setSpeed(0.001f))
                .setDamage(8.0d)
                .setName("Fire Sprayer")
                .setDescription("A long range weapon that can shoot fire lasers in front of you! How cool is that..."));

        setUltimate(new UltimateTalent(
                "Run it Back", """
                Instantly place a marker at your current location for {duration}.
                                        
                Upon death or after duration ends, safely teleport to the marked location with health you had upon activating the ability.
                """,
                80
        )
                .setType(Talent.Type.ENHANCE)
                .setItem(Material.TOTEM_OF_UNDYING)
                .setDurationSec(6)
                .setCooldownSec(40));

    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final Location location = getSafeLocation(player.getLocation());
        final double health = player.getHealth();

        setUsingUltimate(player, true);

        markerLocation.put(player, new RunInBackData(player, location, health));
        player.setVisualFire(true);

        new TimedGameTask(getUltimateDuration()) {
            @Override
            public void run(int tick) {
                if (getMarker(player) == null) {
                    player.setVisualFire(false);
                    cancel();
                    return;
                }

                final int percent = tick * 10 / maxTick;
                player.sendSubtitle("&6🔥".repeat(percent) + "&8🔥".repeat(10 - percent), 0, 10, 0);

                // Fx
                player.spawnWorldParticle(player.getLocation().add(0.0d, 0.5d, 0.0d), Particle.FLAME, 1, 1.0d, 0.5d, 1.0d, 0.05f);

                // Fx at marker
                player.spawnWorldParticle(location, Particle.LANDING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                player.spawnWorldParticle(location, Particle.DRIP_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
            }

            @Override
            public void onLastTick() {
                rebirthPlayer(player);
            }
        }.runTaskTimer(0, 1);

        return UltimateCallback.OK;
    }

    @Override
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        return isSafeLocation(player.getLocation());
    }

    @Override
    public String predicateMessage(@Nonnull GamePlayer player) {
        return "Location is not safe!";
    }

    public void rebirthPlayer(@Nonnull GamePlayer player) {
        final RunInBackData data = markerLocation.remove(player);

        if (data == null) {
            return;
        }

        final double health = data.health();

        player.setVisualFire(false);
        player.setInvulnerable(true);
        player.setHealth(health);

        final Location teleportLocation = data.location();
        teleportLocation.setYaw(player.getYaw());
        teleportLocation.setPitch(player.getPitch());

        player.teleport(teleportLocation);

        // Reload
        if (getWeapon() instanceof RangeWeapon rangeWeapon) {
            rangeWeapon.forceReload(player);
        }

        // Fx
        player.addPotionEffect(PotionEffectType.SLOW, 20, 50);
        player.playWorldSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.5f);
        player.spawnWorldParticle(Particle.FIREWORKS_SPARK, 50, 0.1d, 0.5d, 0.1d, 0.2f);
        player.spawnWorldParticle(Particle.LAVA, 10, 0.1d, 0.5d, 0.1d, 0.2f);
        player.sendTitle("&6🔥", "&eOn Rebirth...", 5, 10, 5);

        GameTask.runLater(() -> player.setInvulnerable(false), 20);
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final EnumDamageCause cause = input.getDamageCause();

        if (!validatePlayer(player) || cause == null) {
            return null;
        }

        // Check for ultimate death
        if (isUsingUltimate(player) && input.getDamage() >= player.getHealth()) {
            rebirthPlayer(player);
            setUsingUltimate(player, false);

            return DamageOutput.CANCEL;
        }

        // Cancel any fire damage
        return switch (cause) {
            case FIRE, FIRE_TICK, LAVA -> DamageOutput.CANCEL;
            default -> null;
        };
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 1);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.SPARK_MOLOTOV.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SPARK_FLASH.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.FIRE_GUY.getTalent();
    }

    private RunInBackData getMarker(GamePlayer player) {
        return markerLocation.get(player);
    }

    private boolean isSafeLocation(Location location) {
        return getSafeLocation(location) != null;
    }

    private Location getSafeLocation(Location location) {
        final World world = location.getWorld();

        if (world == null) {
            return null;
        }

        for (int i = 10; i > 0; i--) {
            if (location.getBlock().getRelative(BlockFace.DOWN).getType().isOccluding()) {
                break;
            }

            location.subtract(0, 1, 0);
        }

        return location;
    }
}
