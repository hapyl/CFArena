package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Random;

public class Geyser {

    private final Location location;
    private final Direction direction;
    private final int range;
    private final int minDelay;
    private final int maxDelay;
    private final int duration;

    private GameTask task;

    public Geyser(int x, int y, int z, Direction direction, int range, int minDelay, int maxDelay, int duration) {
        this.location = new Location(Bukkit.getWorlds().get(0), x, y, z);
        this.direction = direction;
        this.range = range;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.duration = duration;
    }

    public void useGeyser() {

    }

    public void nextTask() {
        if (task != null) {
            task.cancel();
        }

        int nextDelay = new Random().nextInt(minDelay, maxDelay);

        task = new GameTask() {
            @Override
            public void run() {
                useGeyser();
            }
        }.runTaskLater(nextDelay);
    }
}
