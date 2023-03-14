package me.hapyl.fight.game.maps.features;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.util.Direction;
import me.hapyl.spigotutils.module.math.Tick;

import java.util.Set;

public class WinerySteamFeature extends MapFeature {

    private final Set<Geyser> geysers;

    public WinerySteamFeature() {
        super("Hot Steam", "Be careful of the hot steam!");

        geysers = Sets.newHashSet();
        geysers.add(new Geyser(182, 67, 214, Direction.NORTH, Tick.fromSecond(4), Tick.fromSecond(10), 1, 1));
    }

    @Override
    public void onStart() {
        geysers.forEach(Geyser::nextTask);
    }

    @Override
    public void tick(int tick) {
    }
}
