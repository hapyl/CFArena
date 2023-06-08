package me.hapyl.fight.game.talents.archive.freazly;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BarrierWall extends GameTask implements Removable {

    private final List<Block> blocks;
    private final Location location;
    private final Player player;
    private final IceBarrier talent;

    public BarrierWall(@Nonnull Player player, @Nonnull Location location, @Nonnull IceBarrier talent) {
        this.blocks = Lists.newArrayList();
        this.player = player;
        this.location = BukkitUtils.newLocation(location);
        this.talent = talent;
    }

    public BarrierWall add(Block block) {
        blocks.add(block);
        return this;
    }

    public void build() {
        final List<Block> blocks = getBlocks();

        if (blocks.isEmpty()) {
            return;
        }

        iterateBlocks(0, 5, talent.buildDelay);
        iterateBlocks(5, 10, talent.buildDelay * 2);
        iterateBlocks(10, 15, talent.buildDelay * 3, () -> {
            // Start melting
            runTaskTimer(0, 20);

            if (talent.getDuration() > 0) {
                melt();
            }
        });

    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    @Override
    public void remove() {
        for (Block block : blocks) {
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 10, 2f);
            block.setType(Material.AIR, false);
        }

        cancel();
        blocks.clear();
        talent.removeMapped(player);
    }

    @Override
    public void run() {
        for (LivingEntity entity : Utils.getEntitiesInRangeValidateRange(location, talent.radius)) {
            if (entity == player) {
                GamePlayer.getPlayer(player).heal(talent.healingPerTick);
            }
            else {
                entity.addPotionEffect(PotionEffectType.SLOW.createEffect(20, 3));
            }
        }

        Geometry.drawCircle(location, talent.radius, Quality.SUPER_HIGH, new WorldParticle(Particle.FALLING_WATER));
        Geometry.drawCircle(location, talent.radius, Quality.SUPER_HIGH, new WorldParticle(Particle.BUBBLE_POP));
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

    private void setBlock(Block block) {
        block.setType(Material.ICE, false);

        PlayerLib.playSound(block.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 0.8f);
        PlayerLib.playSound(block.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 1.6f);
    }

    private void melt() {
        final int delay = talent.getDuration() / 4;

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
                    remove();
                    cancel();
                }
            }
        }.runTaskTimer(0, delay);

    }

}