package me.hapyl.fight.game.maps.features;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.Direction;
import me.hapyl.fight.game.maps.LevelFeature;
import me.hapyl.fight.game.maps.winery.Steam;

import java.util.Set;

public class WinerySteamFeature extends LevelFeature {

    private final Set<Steam> geysers;

    public WinerySteamFeature() {
        super("Hot Steam", """
                "Be careful of the hot steam!"
                """);

        geysers = Sets.newHashSet();

        createGeyser(4982, 67, 14, Direction.NORTH)
                .setMinDelay(Tick.fromSeconds(8))
                .setMaxDelay(Tick.fromSeconds(16))
                .setDuration(Tick.fromSeconds(2))
                .setRange(6);

        createGeyser(4983, 67, 21, Direction.EAST)
                .setMinDelay(Tick.fromSeconds(8))
                .setMaxDelay(Tick.fromSeconds(16))
                .setDuration(Tick.fromSeconds(2))
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
