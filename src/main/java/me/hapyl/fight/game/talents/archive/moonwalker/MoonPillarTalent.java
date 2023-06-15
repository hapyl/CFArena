package me.hapyl.fight.game.talents.archive.moonwalker;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.CreationTalent;
import me.hapyl.fight.game.talents.TickingCreation;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MoonPillarTalent extends CreationTalent {

    @DisplayField protected final int pulseInterval = 15;
    @DisplayField(suffix = "&f❤") protected final double healingPerPulse = 2.0d;
    @DisplayField protected final double pulseDamage = 5.0d;
    @DisplayField(suffix = "blocks") protected final double pulseRange = 2.5d;

    private final BlockData PARTICLE_DATA = Material.END_STONE.createBlockData();

    public MoonPillarTalent() {
        super("Moonslite Pillar", """
                Raise a pillar at &etarget &7location for {duration} &7that pulses in set intervals, damaging enemies and healing yourself.
                                        
                &6;;You can only have 1 pillar at a time.
                """
        );

        setItem(Material.BONE);
        setDurationSec(10);
        setCooldownSec(30);
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

        newCreation(player, new TickingCreation() {

            @Override
            public void run(int tick) {
                if (location.getBlock().getType().isAir() || (tick >= getDuration())) {
                    removeCreation(player, this);
                    return;
                }

                pulse();
            }

            @Override
            public void create(Player player) {
                location.getBlock().setType(Material.END_STONE_BRICKS, false);

                // Added a little extend animation
                GameTask.runLater(() -> location.getBlock().getRelative(BlockFace.UP).setType(Material.END_STONE_BRICKS, false), 2);
                GameTask.runLater(() -> location.getBlock().getRelative(BlockFace.UP, 2).setType(Material.END_STONE, false), 4);

                PlayerLib.playSound(location, Sound.BLOCK_PISTON_EXTEND, 0.25f);

                // Start task
                setIncrement(pulseInterval);
                runTaskTimer(6/*compensate animation*/, pulseInterval);
            }

            @Override
            public void remove() {
                location.getBlock().setType(Material.AIR, false);
                location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR, false);
                location.getBlock().getRelative(BlockFace.UP, 2).setType(Material.AIR, false);

                //fx
                PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
                Utils.getWorld(location).spawnParticle(Particle.SPIT, location.clone().add(0, 2, 0), 15, 0, 1, 0, 0.05);

                cancelIfActive();
            }

            private void pulse() {
                Geometry.drawCircle(
                        location,
                        pulseRange,
                        Quality.HIGH,
                        new BlockDataDraw(Particle.BLOCK_DUST, PARTICLE_DATA)
                );

                PlayerLib.playSound(location, Sound.BLOCK_STONE_BREAK, 0.0f);

                Collect.nearbyLivingEntities(location, pulseRange).forEach(entity -> {
                    if (entity == player) {
                        GamePlayer.getPlayer(player).heal(healingPerPulse);
                        PlayerLib.addEffect(player, PotionEffectType.JUMP, 20, 2);
                        return;
                    }

                    GamePlayer.damageEntity(entity, pulseDamage, player, EnumDamageCause.MOON_PILLAR);

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

    private Block getTargetBlock(Player player) {
        final Block block = player.getTargetBlockExact(7);

        return block == null ? null : block.getRelative(BlockFace.UP);
    }

}
