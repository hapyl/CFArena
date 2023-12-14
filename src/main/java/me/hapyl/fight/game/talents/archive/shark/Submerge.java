package me.hapyl.fight.game.talents.archive.shark;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.game.task.TimedGameTask;

public class Submerge extends TimedGameTask {

    private final GamePlayer player;

    public Submerge(Timed timed, GamePlayer player) {
        super(timed);

        this.player = player;
        runTaskTimer(0, 1);
    }

    @Override
    public void run(int tick) {

    }
}
