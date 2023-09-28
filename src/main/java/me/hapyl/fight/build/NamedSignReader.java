package me.hapyl.fight.build;

import com.google.common.collect.Lists;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NamedSignReader {

    private final World world;

    public NamedSignReader(World world) {
        this.world = world;
    }

    public Queue<Sign> readAsQueue() {
        final List<Sign> signs = read();
        signs.sort(Comparator.comparing(sign -> sign.getSide(Side.FRONT).getLine(0)));

        return new LinkedList<>(signs);
    }

    public List<Sign> read() {
        final List<Sign> list = Lists.newArrayList();

        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState tile : chunk.getTileEntities()) {
                final BlockData blockData = tile.getBlockData();

                if (blockData instanceof org.bukkit.block.data.type.Sign) {
                    final Sign sign = (Sign) tile;

                    if (check(sign)) {
                        list.add(sign);
                    }
                }
            }
        }

        return list;
    }

    public boolean check(Sign sign) {
        final SignSide side = sign.getSide(Side.FRONT);
        final String line = side.getLine(0);

        return line.startsWith("[") && line.endsWith("]");
    }

}
