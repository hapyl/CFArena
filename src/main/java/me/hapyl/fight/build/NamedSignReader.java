package me.hapyl.fight.build;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class NamedSignReader {

    private final World world;

    public NamedSignReader(World world) {
        this.world = world;
    }

    public Queue<Sign> readAsQueue() {
        final List<Sign> list = read();

        list.sort((a, b) -> {
            final NamedSign namedA = NamedSign.fromSign(a);
            final NamedSign namedB = NamedSign.fromSign(b);

            if (namedA == null || namedB == null) {
                return 0;
            }

            return Integer.compare(namedA.ordinal(), namedB.ordinal());
        });

        return new LinkedList<>(list);
    }

    public Map<NamedSign, List<Sign>> readAsMap() {
        final Map<NamedSign, List<Sign>> map = Maps.newLinkedHashMap();

        for (Sign sign : read()) {
            final NamedSign named = NamedSign.fromSign(sign);

            if (named == null) {
                continue;
            }

            map.compute(named, (type, list) -> {
                if (list == null) {
                    list = Lists.newArrayList();
                }

                list.add(sign);
                return list;
            });
        }

        return map;
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
