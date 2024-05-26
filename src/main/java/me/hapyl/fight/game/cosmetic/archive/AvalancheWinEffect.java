package me.hapyl.fight.game.cosmetic.archive;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.WinCosmetic;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class AvalancheWinEffect extends WinCosmetic {

    private final Set<Block> blocks;
    private final BlockData blockData;

    public AvalancheWinEffect() {
        super("Avalanche", "Freeze!", Rarity.MYTHIC);

        blocks = Sets.newHashSet();
        blockData = Material.ICE.createBlockData();

        setIcon(Material.ICE);
        setMaxTimes(50);
        setStep(2);
    }

    @Override
    public void onStart(Display display) {

    }

    @Override
    public void onStop(Display display) {
        blocks.forEach(block -> block.getState().update(true, false));
        blocks.clear();
    }

    @Override
    public void tickTask(Display display, int tick) {
        final Location location = display.getLocation();
        final float pitchPerTick = 2.0f / getMaxTimes();

        getBlocks(location, getMaxTimes() - tick).forEach(block -> {
            if (blocks.add(block)) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    //online.sendBlockChange(block.getLocation(), CollectionUtils.randomElement(data, data[0]));
                    online.sendBlockChange(block.getLocation(), blockData);
                }

                // Fx
                final Location fxLocation = block.getLocation().add(0.0d, 1.0d, 0.0d);

                PlayerLib.spawnParticle(fxLocation, Particle.ITEM_SNOWBALL, 1, 0.15d, 0.25d, 0.15d, 0.05f);
                PlayerLib.spawnParticle(fxLocation, Particle.SNOWFLAKE, 2, 0.1d, 0.25d, 0.11d, 0.01f);

                PlayerLib.playSound(
                        fxLocation,
                        Sound.BLOCK_SNOW_BREAK,
                        (float) Numbers.clamp(2.0f - (pitchPerTick * tick), 0.0d, 2.0d)
                );
            }
        });
    }

    private List<Block> getBlocks(Location location, int radius) {
        final World world = location.getWorld();
        final List<Block> list = Lists.newArrayList();

        if (world == null) {
            return list;
        }

        int blockX = location.getBlockX();
        int blockZ = location.getBlockZ();

        for (int x = blockX - radius; x <= blockX + radius; ++x) {
            for (int z = blockZ - radius; z <= blockZ + radius; ++z) {
                if ((blockX - x) * (blockX - x) + (blockZ - z) * (blockZ - z) <= (radius * radius)) {

                    int y = location.getBlockY();
                    Block block = world.getBlockAt(x, y, z);

                    // Get highest or lowest block
                    while (block.getType().isSolid()) {
                        block = world.getBlockAt(x, ++y, z);
                        if (y >= world.getMaxHeight()) {
                            break;
                        }
                    }

                    while (block.getType().isAir()) {
                        block = world.getBlockAt(x, --y, z);
                        if (y <= world.getMinHeight()) {
                            break;
                        }
                    }

                    if (block.getType().isSolid()) {
                        list.add(block);
                    }
                }
            }
        }

        return list;
    }

}
