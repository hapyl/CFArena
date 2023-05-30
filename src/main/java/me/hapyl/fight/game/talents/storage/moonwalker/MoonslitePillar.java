package me.hapyl.fight.game.talents.storage.moonwalker;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.Map;

public class MoonslitePillar extends Talent {

    private final Map<Player, Location> pillars = Maps.newHashMap();

    @DisplayField private final int pulseInterval = 15;
    @DisplayField(suffix = "&f❤") private final double healingPerPulse = 2.0d;
    @DisplayField private final double pulseDamage = 5.0d;
    @DisplayField(suffix = "blocks") private final double pulseRange = 2.5d;

    public MoonslitePillar() {
        super("Moonslite Pillar", """
                Raise a pillar at &etarget &7location for {duration} &7that pulses in set intervals, damaging enemies and healing yourself.
                                        
                &6;;You can only have 1 pillar at a time.
                """, Type.COMBAT
        );

        setDurationSec(10);
        setItem(Material.BONE);
        setCooldownSec(30);
    }

    @Override
    public void onStop() {
        pillars.values().forEach(this::destroyPillar);
        pillars.clear();
    }

    @Override
    public void onDeath(Player player) {
        Nulls.runIfNotNull(pillars.get(player), this::destroyPillar);
        pillars.remove(player);
    }

    @Override
    public Response execute(Player player) {
        final Block block = getTargetBlock(player);
        if (block == null) {
            return Response.error("No valid target block!");
        }

        final Location location = block.getLocation().add(0.5d, 0.0d, 0.5d);
        if (!canFit(location)) {
            return Response.error("Cannot fit pillar!");
        }

        if (pillars.containsKey(player)) {
            destroyPillar(location);
        }

        pillars.put(player, location);
        raisePillar(location);

        final int period = 5;

        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                if (location.getBlock().getType().isAir() || ((tick += period) >= getDuration())) {
                    destroyPillar(location);
                    pillars.remove(player);
                    cancel();
                    return;
                }

                // pulse
                if (tick % pulseInterval == 0) {
                    pulsePillar(location, player);
                }
            }
        }.addCancelEvent(() -> destroyPillar(location)).runTaskTimer(0, period);

        return Response.OK;
    }

    private void raisePillar(Location location) {
        location.getBlock().setType(Material.END_STONE_BRICKS, false);

        // Added a little extend animation
        GameTask.runLater(() -> {
            location.getBlock().getRelative(BlockFace.UP).setType(Material.END_STONE_BRICKS, false);
        }, 2);
        GameTask.runLater(() -> {
            location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.END_STONE, false);
        }, 4);

        PlayerLib.playSound(location, Sound.BLOCK_PISTON_EXTEND, 0.25f);
    }

    private void destroyPillar(Location location) {
        if (location == null || location.getBlock().getType().isAir()) {
            return;
        }

        location.getBlock().setType(Material.AIR, false);
        location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR, false);
        location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR, false);

        //fx
        PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
        Utils.getWorld(location).spawnParticle(Particle.SPIT, location.clone().add(0, 2, 0), 15, 0, 1, 0, 0.05);
    }

    private void pulsePillar(Location location, Player owner) {
        final BlockData blockData = Material.END_STONE.createBlockData();

        Geometry.drawCircle(location, pulseRange, Quality.NORMAL, new Draw(Particle.BLOCK_DUST) {
            @Override
            public void draw(Location location) {
                final World world = location.getWorld();
                if (world != null) {
                    world.spawnParticle(this.getParticle(), location, 0, 0, 0, 0, blockData);
                }
            }
        });

        PlayerLib.playSound(location, Sound.BLOCK_STONE_BREAK, 0.0f);

        Utils.getEntitiesInRange(location, pulseRange).forEach(entity -> {
            if (entity == owner) {
                GamePlayer.getPlayer(owner).heal(healingPerPulse);
                PlayerLib.addEffect(owner, PotionEffectType.JUMP, 20, 2);
                PlayerLib.spawnParticle(owner.getEyeLocation().add(0.0d, 0.5d, 0.0d), Particle.HEART, 1, 0, 0, 0, 0);
            }
            else {
                GamePlayer.damageEntity(entity, pulseDamage, owner, EnumDamageCause.MOON_PILLAR);
                entity.setVelocity(entity.getLocation().getDirection().multiply(-0.5).setY(0.25d));
            }
        });
    }

    @Nullable
    public Location getPillar(Player player) {
        return this.pillars.getOrDefault(player, null);
    }

    private boolean canFit(Location location) {
        final Block block = location.getBlock();
        return block.getType().isAir() && block.getRelative(BlockFace.UP).getType().isAir() &&
                block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isAir();
    }

    private Block getTargetBlock(Player player) {
        final Block block = player.getTargetBlockExact(7);
        if (block == null) {
            return null;
        }
        return block.getRelative(BlockFace.UP);
    }

}
