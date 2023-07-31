package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MoonwalkerUltimate extends UltimateTalent {

    @DisplayField private final int corrosionTime = 130;
    @DisplayField private final double meteoriteRadius = 8.5d;
    @DisplayField private final double meteoriteDamage = 50.0d;

    public MoonwalkerUltimate() {
        super(
                "Moonteorite",
                "Summons meteorite at the &etarget &7location. Upon landing, it creates a huge explosion dealing with massive damage and applying &6&lCorrosion &7for &b{corrosionTime}s&7.",
                80
        );

        setCooldownSec(45);
        setItem(Material.END_STONE_BRICKS);
    }

    public void useUltimate(Player player) {
        final int distanceFromLanding = 15;
        final Block targetBlock = Heroes.MOONWALKER.getHero(Moonwalker.class).getTargetBlock(player);
        if (targetBlock == null) {
            return;
        }

        final Location playerLocation = targetBlock.getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
        final Location startLocation = playerLocation.clone().add(distanceFromLanding, distanceFromLanding, distanceFromLanding);

        PlayerLib.playSound(player, Sound.ENTITY_WITHER_DEATH, 0.0f);

        new GameTask() {
            private int tick = -distanceFromLanding;

            @Override
            public void run() {
                // Only spawn meteorite after delay
                if (tick >= 0) {
                    createBlob(startLocation.clone(), (tick == distanceFromLanding + 1));
                    startLocation.subtract(1, 1, 1);
                }
                else {
                    PlayerLib.spawnParticle(startLocation, Particle.SPELL_WITCH, 50, 1.25d, 0.25, 1.25, 1.0f);
                }

                // Last tick (explode and stop sound)
                if (tick >= (distanceFromLanding + 1)) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.stopSound(Sound.ENTITY_WITHER_DEATH, PlayerLib.SOUND_CATEGORY));
                    explode(player, playerLocation);
                    this.cancel();
                    return;
                }

                // Notify players in range that they're in danger
                Collect.nearbyPlayers(playerLocation, meteoriteRadius).forEach(target -> {
                    target.sendWarning("Meteorite Warning!", 5);
                });

                Geometry.drawCircle(playerLocation, meteoriteRadius, Quality.NORMAL, new WorldParticle(Particle.CRIT));
                Geometry.drawCircle(playerLocation, meteoriteRadius + 0.25d, Quality.VERY_HIGH, new WorldParticle(Particle.SNOW_SHOVEL));

                ++tick;
            }

        }.runTaskTimer(0, 1);
    }

    public void createBlob(Location center, boolean last) {
        PlayerLib.spawnParticle(center, Particle.LAVA, 10, 1, 1, 1, 0);

        // Clear previous blob
        this.clearTrash(center.clone());

        // Move location to the next step
        center.subtract(1, 0, 1);

        final Set<Block> savedBlocks = new HashSet<>();

        // Spawn inner layer
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE_BRICKS);
                // only save the last iteration
                if (last) {
                    savedBlocks.add(block);
                }
            }
        }

        // Spawn outer layer
        center.add(0, 1, 0);
        fillOuter(center, last ? savedBlocks : null);

        // Spawn outer layer 2
        center.subtract(0, 2, 0);
        fillOuter(center, last ? savedBlocks : null);

        if (last) {
            for (Block savedBlock : savedBlocks) {
                savedBlock.getState().update(false, false);
            }
            savedBlocks.clear();
        }

    }

    private Block sendChange(Location location, Material material) {
        final BlockData data = material.createBlockData();
        Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, data));
        return location.getBlock();
    }

    private void fillOuter(Location center, Set<Block> blocks) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i == 0 || i == 2) && j != 1) {
                    continue;
                }
                final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE);
                if (blocks != null) {
                    blocks.add(block);
                }
            }
        }
    }

    private void clearTrash(Location center) {
        center.add(0, 2, 0);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i == 0 || i == 2) && j != 1) {
                    continue;
                }
                center.clone().subtract(i, 0, j).getBlock().getState().update(false, false);
            }
        }

        center.subtract(0, 1, 0);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (((i == 1 || i == 2) && j == 2) || (i == 2 && j == 1)) {
                    continue;
                }
                center.clone().subtract(i, 0, j).getBlock().getState().update(false, false);
            }
        }

        center.subtract(0, 1, 0);
        center.clone().subtract(1, 0, 0).getBlock().getState().update(false, false);
        center.clone().subtract(0, 0, 1).getBlock().getState().update(false, false);
    }

    private void explode(Player executor, Location location) {
        final World world = location.getWorld();

        if (world == null) {
            throw new NullPointerException("world is null");
        }

        Collect.nearbyEntities(location, meteoriteRadius).forEach(entity -> {
            entity.damage(meteoriteDamage, CF.getEntity(executor), EnumDamageCause.METEORITE);
            entity.addEffect(GameEffectType.CORROSION, corrosionTime, true);
        });

        // FX
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_HUGE, 1, 0.8d, 0.2d, 0.8d, 0.0f);
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 15, 5d, 2d, 5d, 0.0f);

        PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_WITHER_HURT, 0.25f);
        PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_HURT, 0.5f);
    }

}
