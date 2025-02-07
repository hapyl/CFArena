package me.hapyl.fight.game.heroes.spark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class Spark extends Hero {

    private final PlayerMap<RunInBackData> markerLocation = PlayerMap.newMap();
    @DisplayField private final double inWaterDamage = 3d;

    public Spark(@Nonnull Key key) {
        super(key, "Spark");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.RANGE, Archetype.POWERFUL_ULTIMATE, Archetype.SELF_SUSTAIN);
        profile.setGender(Gender.MALE);

        setDescription("Strikes with fire! ...literally.");
        setItem("ade095332720215ca9b85e7eacd1d092b1697fad34d696add94d3b70976702c");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestPlate(Color.ORANGE);
        equipment.setLeggings(Color.RED);
        equipment.setBoots(Color.ORANGE);

        setWeapon(new SparkWeapon());
        setUltimate(new SparkUltimate());
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
        player.addEffect(Effects.SLOW, 50, 20);
        player.playWorldSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.5f);
        player.spawnWorldParticle(Particle.FIREWORK, 50, 0.1d, 0.5d, 0.1d, 0.2f);
        player.spawnWorldParticle(Particle.LAVA, 10, 0.1d, 0.5d, 0.1d, 0.2f);
        player.sendTitle("&6🔥", "&eOn Rebirth...", 5, 10, 5);

        GameTask.runLater(() -> player.setInvulnerable(false), 20);
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final EnumDamageCause cause = instance.getCause();

        if (!validatePlayer(player) || cause == null) {
            return;
        }

        // Check for ultimate death
        if (player.isUsingUltimate() && instance.getDamage() >= player.getHealth()) {
            rebirthPlayer(player);
            player.setUsingUltimate(false);

            instance.setCancelled(true);
            return;
        }

        // Cancel any fire damage
        switch (cause) {
            case FIRE, FIRE_TICK, LAVA -> instance.setCancelled(true);
        }
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                getAlivePlayers().forEach(player -> {
                    if (player.isInWater()) {
                        player.damage(inWaterDamage, EnumDamageCause.WATER);
                        player.playWorldSound(Sound.ENTITY_PLAYER_HURT_DROWN, 0.75f);
                    }
                });
            }
        }.runTaskTimer(10, 10);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addEffect(Effects.FIRE_RESISTANCE, 1, 999999);
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.SPARK_MOLOTOV;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.SPARK_FLASH;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.FIRE_GUY;
    }

    private RunInBackData getMarker(GamePlayer player) {
        return markerLocation.get(player);
    }

    private Location getSafeLocation(Location location) {
        final World world = location.getWorld();

        if (world == null) {
            return null;
        }

        for (int i = 10; i > 0; i--) {
            if (location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                return location;
            }

            location.subtract(0, 1, 0);
        }

        return null;
    }

    private class SparkUltimate extends UltimateTalent {
        public SparkUltimate() {
            super(Spark.this, "Run it Back", 80);

            setDescription("""
                    Instantly place a marker at your current location for {duration}.
                    
                    Upon death or after duration ends, safely teleport to the marked location with health you had upon activating the talent.
                    """
            );

            setType(TalentType.ENHANCE);
            setItem(Material.TOTEM_OF_UNDYING);
            setDurationSec(6);
            setCooldownSec(40);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            final Location location = getSafeLocation(player.getLocation());

            if (location == null) {
                return error("Location is not safe!");
            }

            final double health = player.getHealth();

            return builder()
                    .onExecute(() -> {
                        markerLocation.put(player, new RunInBackData(player, location, health));
                        player.setVisualFire(true);
                    })
                    .onTick(tick -> {
                        if (getMarker(player) == null) {
                            player.setVisualFire(false);
                            return;
                        }

                        final int percent = tick * 10 / getDuration();
                        player.sendSubtitle("&6🔥".repeat(percent) + "&8🔥".repeat(10 - percent), 0, 10, 0);

                        // Fx
                        player.spawnWorldParticle(player.getLocation().add(0.0d, 0.5d, 0.0d), Particle.FLAME, 1, 1.0d, 0.5d, 1.0d, 0.05f);

                        // Fx at marker
                        player.spawnWorldParticle(location, Particle.LANDING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                        player.spawnWorldParticle(location, Particle.DRIPPING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                    })
                    .onEnd(() -> {
                        rebirthPlayer(player);
                    });
        }
    }
}
