package me.hapyl.fight.game.talents.archive.moonwalker;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.CreationTalent;
import me.hapyl.fight.game.talents.TickingDisplayCreation;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class MoonPillarTalent extends CreationTalent {

    @DisplayField protected final int pulseInterval = 15;
    @DisplayField(suffix = "&f❤") protected final double healingPerPulse = 2.0d;
    @DisplayField protected final double pulseDamage = 5.0d;
    @DisplayField(suffix = "blocks") protected final double pulseRange = 2.5d;

    private final BlockData PARTICLE_DATA = Material.END_STONE.createBlockData();
    private final DisplayData DISPLAY_DATA = BlockStudioParser.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0000f,-1.3125f,0.0000f,1.1875f,1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,0.0000f,1.1250f,0.3438f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[1.0000f,0.0000f,0.0000f,0.3750f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[-0.0000f,0.0000f,-1.0000f,0.6250f,0.0000f,1.0000f,0.0000f,-0.5000f,1.0000f,0.0000f,-0.0000f,0.4375f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0000f,-1.3125f,0.0000f,1.1875f,1.0000f,-0.0000f,0.0000f,1.7500f,0.0000f,0.0000f,1.1250f,0.3438f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0000f,1.3125f,-0.0000f,-0.1875f,1.0000f,-0.0000f,0.0000f,0.2500f,-0.0000f,-0.0000f,-1.1250f,0.6563f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[-1.0000f,0.0000f,-0.0000f,0.6250f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,-1.0000f,0.6250f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[0.0000f,0.0000f,1.0000f,0.3750f,0.0000f,1.0000f,0.0000f,-0.5000f,-1.0000f,0.0000f,0.0000f,0.5625f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0000f,1.3125f,-0.0000f,-0.1875f,1.0000f,-0.0000f,0.0000f,1.7500f,-0.0000f,-0.0000f,-1.1250f,0.6563f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    public MoonPillarTalent() {
        super("Moonslite Pillar", """
                Raise a pillar at the &etarget &7location for {duration} &7that pulses in set intervals, damaging enemies and healing yourself.
                                        
                &6;;You can only have one pillar at a time.
                """
        );

        setItem(Material.BONE);
        setDurationSec(10);
        setCooldownSec(30);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Block block = getTargetBlock(player);

        if (block == null) {
            return Response.error("No valid target block!");
        }

        final Location location = block.getLocation().add(0.5d, 0.0d, 0.5d);

        if (!canFit(location)) {
            return Response.error("Cannot fit the pillar!");
        }

        newCreation(player, new TickingDisplayCreation(DISPLAY_DATA) {

            @Override
            public void run(int tick) {
                if (location.getBlock().getType().isAir() || (tick >= getDuration())) {
                    removeCreation(player, this);
                    return;
                }

                pulse();
            }

            @Nonnull
            @Override
            public Location getLocation() {
                return BukkitUtils.newLocation(location).subtract(0.5, 3, 0.5);
            }

            @Override
            public void remove() {
                super.remove();

                location.getBlock().setType(Material.AIR, false);
                location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR, false);
                location.getBlock().getRelative(BlockFace.UP, 2).setType(Material.AIR, false);

                //fx
                PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
                CFUtils.getWorld(location).spawnParticle(Particle.SPIT, location.clone().add(0, 2, 0), 15, 0, 1, 0, 0.05);

                cancel();
            }

            @Override
            public void create(@Nonnull GamePlayer player) {
                super.create(player);

                location.getBlock().setType(Material.BARRIER, false);
                location.getBlock().getRelative(BlockFace.UP).setType(Material.BARRIER, false);
                location.getBlock().getRelative(BlockFace.UP, 2).setType(Material.BARRIER, false);

                new TickingGameTask() {
                    @Override
                    public void run(int tick) {
                        if (tick > 6) {
                            cancel();
                            return;
                        }

                        entity.teleport(entity.getHead().getLocation().add(0, 0.5, 0));
                    }
                }.runTaskTimer(0, 1);

                // Start task
                setIncrement(pulseInterval);
                runTaskTimer(6, pulseInterval);

                PlayerLib.playSound(location, Sound.BLOCK_PISTON_EXTEND, 0.25f);
            }

            private void pulse() {
                Geometry.drawCircle(
                        location,
                        pulseRange,
                        Quality.HIGH,
                        new BlockDataDraw(Particle.BLOCK_DUST, PARTICLE_DATA)
                );

                PlayerLib.playSound(location, Sound.BLOCK_STONE_BREAK, 0.0f);

                Collect.nearbyEntities(location, pulseRange).forEach(entity -> {
                    if (entity.equals(player)) {
                        player.heal(healingPerPulse);
                        player.addPotionEffect(PotionEffectType.JUMP, 20, 2);
                        return;
                    }

                    entity.damage(pulseDamage, player, EnumDamageCause.MOON_PILLAR);

                    // Push the opposite direction from the pilar
                    final Vector vector = location.toVector()
                            .subtract(entity.getLocation().toVector())
                            .normalize()
                            .multiply(-1)
                            .multiply(0.6d)
                            .setY(0.25d);
                    entity.setVelocity(vector);

                });
            }
        });

        return Response.OK;
    }

    private boolean canFit(Location location) {
        final Block block = location.getBlock();

        return block.isEmpty()
                && block.getRelative(BlockFace.UP).isEmpty()
                && block.getRelative(BlockFace.UP, 2).isEmpty();
    }

    private Block getTargetBlock(GamePlayer player) {
        final Block block = player.getTargetBlockExact(7);

        return block == null ? null : block.getRelative(BlockFace.UP);
    }

}
