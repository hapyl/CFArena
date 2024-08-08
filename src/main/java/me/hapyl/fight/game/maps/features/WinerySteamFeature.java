package me.hapyl.fight.game.maps.features;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.maps.winery.Steam;
import me.hapyl.fight.util.Direction;
import me.hapyl.eterna.module.math.Tick;

import java.util.Set;

public class WinerySteamFeature extends MapFeature {

    private final Set<Steam> geysers;

    public WinerySteamFeature() {
        super("Hot Steam", """
                "Be careful of the hot steam!"
                """);

        geysers = Sets.newHashSet();

        createGeyser(4982, 67, 14, Direction.NORTH)
                .setMinDelay(Tick.fromSecond(8))
                .setMaxDelay(Tick.fromSecond(16))
                .setDuration(Tick.fromSecond(2))
                .setRange(6);

        createGeyser(4983, 67, 21, Direction.EAST)
                .setMinDelay(Tick.fromSecond(8))
                .setMaxDelay(Tick.fromSecond(16))
                .setDuration(Tick.fromSecond(2))
                .setRange(4);

        createGeyser(4986, 62, 27, Direction.DOWN);
        createGeyser(4984, 62, 21, Direction.DOWN);
    }

    private Steam createGeyser(int x, int y, int z, Direction direction) {
        final Steam geyser = new Steam(x, y, z, direction);

        geysers.add(geyser);
        return geyser;
    }

    @Override
    public void onStart() {
        geysers.forEach(geyser -> {
            geyser.createEntities();
            geyser.start();
        });
    }

}
