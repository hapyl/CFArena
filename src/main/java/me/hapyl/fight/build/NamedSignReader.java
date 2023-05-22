package me.hapyl.fight.build;

import com.google.common.collect.Lists;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class NamedSignReader {

    private final World world;

    public NamedSignReader(World world) {
        this.world = world;
    }

    public List<Sign> read() {
        final List<Sign> list = Lists.newArrayList();

        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState tile : chunk.getTileEntities()) {
                final BlockData blockData = tile.getBlockData();

                if (blockData instanceof org.bukkit.block.data.type.Sign) {
                    final Sign sign = (Sign) tile;

                    if (NamedSign.check(sign)) {
                        list.add(sign);
                    }
                }
            }
        }

        return list;
    }

}
