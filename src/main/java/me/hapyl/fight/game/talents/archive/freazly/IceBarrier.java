package me.hapyl.fight.game.talents.archive.freazly;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.CreationTalent;
import me.hapyl.fight.game.talents.TickingCreation;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Direction;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.List;

public class IceBarrier extends CreationTalent {

    @DisplayField protected final int buildDelay = 4;
    @DisplayField(suffix = "blocks") protected final double radius = 3.5d;
    @DisplayField protected final double healingPerTick = 2.0d;

    public IceBarrier() {
        super("Ice Barrier");

        setDescription("""
                Creates an ice wall at your &etarget&7 location that melts overtime.
                                
                While active, the wall will periodically unleash freezing energy, healing yourself and slowing enemies down.
                """);

        setItem(Material.PACKED_ICE);
        setCooldownSec(30);
        setDuration(Tick.fromSecond(15));
    }

    @Override
    public Response execute(Player player) {
        if (isExists(player)) {
            return Response.error("Already created!");
        }

        final Location targetLocation = getBuildLocation(player);

        if (targetLocation == null) {
            return Response.error("No valid block in sight!");
        }

        final Direction direction = Direction.getDirection(player.getLocation());
        final boolean isEastWest = direction.isEastOrWest();

        final Location location = targetLocation.subtract((isEastWest ? 0 : 2), 0, (isEastWest ? 2 : 0));

        newCreation(player, new TickingCreation() {
            private final List<Block> blocks = Lists.newArrayList();

            @Override
            public void run(int tick) {
                for (GameEntity entity : Collect.nearbyEntities(location, radius)) {
                    if (entity.is(player)) {
                        entity.heal(healingPerTick);
                    }
                    else {
                        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(20, 3));
                    }
                }

                Geometry.drawCircle(location, radius, Quality.SUPER_HIGH, new WorldParticle(Particle.FALLING_WATER));
                Geometry.drawCircle(location, radius, Quality.SUPER_HIGH, new WorldParticle(Particle.BUBBLE_POP));
            }

            @Override
            public void create(Player player) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 5; j++) {
                        final Block blockToChange = location.add(isEastWest ? 0 : j, i, isEastWest ? j : 0).getBlock();

                        if (blockToChange.getType().isAir()) {
                            blocks.add(blockToChange);
                        }

                        location.subtract(isEastWest ? 0 : j, i, isEastWest ? j : 0);
                    }
                }

                if (blocks.isEmpty()) {
                    return;
                }

                iterateBlocks(0, 5, buildDelay);
                iterateBlocks(5, 10, buildDelay * 2);
                iterateBlocks(10, 15, buildDelay * 3, () -> {
                    // Start melting
                    runTaskTimer(0, 20);

                    if (getDuration() > 0) {
                        melt();
                    }
                });
            }

            @Override
            public void remove() {
                for (Block block : blocks) {
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 10, 2f);
                    block.setType(Material.AIR, false);
                }

                cancel();
                blocks.clear();
            }

            private void setBlock(Block block) {
                block.setType(Material.ICE, false);

                PlayerLib.playSound(block.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 0.8f);
                PlayerLib.playSound(block.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 1.6f);
            }

            private void melt() {
                final int delay = getDuration() / 4;

                new GameTask() {
                    private int currentTick = 0;

                    @Override
                    public void run() {
                        for (Block block : blocks) {
                            block.setType(Material.FROSTED_ICE, false);
                            final Ageable blockData = (Ageable) block.getBlockData();

                            blockData.setAge(Math.min(currentTick, 3));
                            block.setBlockData(blockData, false);
                        }

                        if (currentTick++ > 3) {
                            destroy();
                            cancel();
                        }
                    }
                }.runTaskTimer(0, delay);

            }

            private void destroy() {
                removeCreation(player, this);
            }

            private void iterateBlocks(int start, int end, int delay) {
                iterateBlocks(start, end, delay, null);
            }

            private void iterateBlocks(int start, int end, int delay, @Nullable Runnable runnable) {
                GameTask.runLater(() -> {
                    for (int i = start; i < end; i++) {
                        if (blocks.size() <= i) {
                            break;
                        }
                        setBlock(blocks.get(i));
                    }

                    Nulls.runIfNotNull(runnable, Runnable::run);
                }, delay);
            }
        });

        return Response.OK;
    }

    private Location getBuildLocation(Player player) {
        final Block target = player.getTargetBlockExact(5);

        if (target == null) {
            return null;
        }

        final Block up = target.getRelative(BlockFace.UP);

        if (!up.getType().isAir()) {
            return null;
        }

        return up.getLocation();
    }


}
