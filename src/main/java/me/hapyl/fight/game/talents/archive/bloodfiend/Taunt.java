package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Taunt<T extends Talent> extends GameTask {

    protected final T reference;
    protected final Player player;
    protected final Location initialLocation;
    private int tick;

    public Taunt(T reference, Player player, Location location) {
        this.reference = reference;
        this.player = player;
        this.initialLocation = location;
    }

    public void start(int duration) {
        tick = duration;
        runTaskTimer(0, 1);
    }

    public void remove() {

    }

    public int getDuration() {
        return tick;
    }

    public abstract void run(int tick);

    @Override
    public final void run() {
        run(tick);
        tick--;
    }
}
