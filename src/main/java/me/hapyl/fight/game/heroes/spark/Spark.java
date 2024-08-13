package me.hapyl.fight.game.heroes.spark;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.*;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class Spark extends Hero {

    private final PlayerMap<RunInBackData> markerLocation = PlayerMap.newMap();

    public Spark(@Nonnull Heroes handle) {
        super(handle, "Spark");

        setArchetypes(Archetype.RANGE, Archetype.POWERFUL_ULTIMATE, Archetype.SELF_SUSTAIN);
        setGender(Gender.MALE);

        setDescription("Strikes with fire! ...literally.");
        setItem("ade095332720215ca9b85e7eacd1d092b1697fad34d696add94d3b70976702c");

        final Equipment equipment = this.getEquipment();
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
    public void onStart(@Nonnull GamePlayer player) {
        player.addEffect(Effects.FIRE_RESISTANCE, 1, 999999);
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
            super("Run it Back", 80);

            setDescription("""
                    Instantly place a marker at your current location for {duration}.
                                            
                    Upon death or after duration ends, safely teleport to the marked location with health you had upon activating the talent.
                    """);

            setType(TalentType.ENHANCE);
            setItem(Material.TOTEM_OF_UNDYING);
            setDurationSec(6);
            setCooldownSec(40);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final Location location = getSafeLocation(player.getLocation());

            if (location == null) {
                return UltimateResponse.error("Location is not safe!");
            }

            final double health = player.getHealth();

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
                    player.spawnWorldParticle(location, Particle.DRIPPING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                }

                @Override
                public void onLastTick() {
                    rebirthPlayer(player);
                }
            }.runTaskTimer(0, 1);

            return UltimateResponse.OK;
        }
    }
}
